package com.rookie.controller;

import com.mybatisflex.core.query.QueryWrapper;
import com.rookie.FileServiceClient;
import com.rookie.annotation.AuthRequired;
import com.rookie.aspect.Auth;
import com.rookie.model.FileBuckets;
import com.rookie.model.Token;
import com.rookie.model.dto.*;
import com.rookie.model.entity.UserFavoriteTable;
import com.rookie.model.entity.UserTable;
import com.rookie.model.result.BaseResult;
import com.rookie.model.result.None;
import com.rookie.model.result.ResultCode;
import com.rookie.service.UserFavoriteService;
import com.rookie.service.UserService;
import com.rookie.utils.JWTUtils;
import com.rookie.utils.PasswordUtil;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.jdbc.Null;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.rookie.model.entity.table.UserFavoriteTableTableDef.USER_FAVORITE_TABLE;
import static com.rookie.model.entity.table.UserTableTableDef.USER_TABLE;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private UserFavoriteService userFavoriteService;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expire}")
    private long expire;
    @Resource
    private FileServiceClient fileServiceClient;

    @PostMapping("/reg")
    public BaseResult<None> userRegister(HttpServletRequest req) {
        String name = req.getParameter("name");
        String password = req.getParameter("password");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");

        password = PasswordUtil.encrypt(password);

        var user = userService.getOne(QueryWrapper.create()
                .where(USER_TABLE.NAME.eq(name))
                .where(USER_TABLE.EMAIL.eq(email)));

        if (user != null) {
            return BaseResult.fail("User already exists");
        }

        var newUser = UserTable.builder()
                .name(name)
                .password(password)
                .phone(phone)
                .email(email)
                .createTime(System.currentTimeMillis())
                .updateTime(System.currentTimeMillis())
                .build();

        userService.save(newUser);
        return BaseResult.success();
    }

    @PostMapping("/login")
    public BaseResult<UserLoginDTO> userLogin(HttpServletRequest req) {
        UserTable dbUser;

        var account = req.getParameter("account");
        var password = req.getParameter("password");

        if (account.contains("@")) {
            dbUser = userService.getOne(QueryWrapper.create()
                    .where(USER_TABLE.EMAIL.eq(account)));
        } else {
            dbUser = userService.getOne(QueryWrapper.create()
                    .where(USER_TABLE.PHONE.eq(account)));

        }
        if (dbUser == null) {
            return BaseResult.fail("User does not exist");
        }


        if (!PasswordUtil.check(password, dbUser.getPassword())) {
            return BaseResult.fail("Password error");
        }


        var token = JWTUtils.tokenRelease(new Token(dbUser.getId()), secret, expire);
        return BaseResult.success(UserLoginDTO.builder().token(token).build());
    }

    @AuthRequired
    @GetMapping("/me")
    public BaseResult<UserSelfInfoDTO> getUserSelfInfo(HttpServletRequest req) {
        var userId = Auth.getToken().getId();

        var dbUser = userService.getOneByEntityId(UserTable.ID(userId));
        if (dbUser == null) {
            return BaseResult.fail("User does not exist");
        }

        var userSelfInfo = UserSelfInfoDTO.builder()
                .id(dbUser.getId())
                .name(dbUser.getName())
                .avatar(dbUser.getAvatar())
                .signature(dbUser.getSignature())
                .favCnt(114514) //TODO get fav count
                .colCnt(114514) //TODO get col count
                .updCnt(114514) //TODO get upd count
                .showCollection(dbUser.isShowCollection())
                .build();
        return BaseResult.success(userSelfInfo);
    }

    @GetMapping("/info/{id}")
    public BaseResult<UserInfoDTO> getUserInfo(@PathVariable String id) {
        var dbUser = userService.getOneByEntityId(UserTable.ID(Long.parseLong(id)));

        if (dbUser == null) {
            return BaseResult.fail("User does not exist");
        }

        var userInfo = UserInfoDTO.builder()
                .id(dbUser.getId())
                .name(dbUser.getName())
                .avatar(dbUser.getAvatar())
                .signature(dbUser.getSignature())
                .updCnt(114514) //TODO get upd count
                .colCnt(dbUser.isShowCollection() ? 114514 : -1) //TODO get col count
                .build();

        return BaseResult.success(userInfo);
    }

    @AuthRequired
    @GetMapping("/follower")
    public BaseResult<UserListDTO> getFollowers(HttpServletRequest req) {
        var userId = Auth.getToken().getId();
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        List<UserFavoriteTable> favlist = userFavoriteService
                .list(QueryWrapper
                        .create()
                        .from(USER_FAVORITE_TABLE)
                        .where(USER_FAVORITE_TABLE.UID.eq(userId))
                        .offset(offset)
                        .limit(size));
        return getUserListDTOBaseResult(favlist);
    }

    @AuthRequired
    @GetMapping("/fans")
    public BaseResult<UserListDTO> getFans(HttpServletRequest req) {
        var userId = Auth.getToken().getId();
        Integer offset = Integer.valueOf(req.getParameter("offset"));
        Integer size = Integer.valueOf(req.getParameter("size"));
        List<UserFavoriteTable> favlist = userFavoriteService
                .list(QueryWrapper
                        .create()
                        .from(USER_FAVORITE_TABLE)
                        .where(USER_FAVORITE_TABLE.FAV_UID.eq(userId))
                        .offset(offset)
                        .limit(size));
        return getUserListDTOBaseResult(favlist);

    }

    @NotNull
    private BaseResult<UserListDTO> getUserListDTOBaseResult(List<UserFavoriteTable> favlist) {
        List<UserSimpleInfo> ulist = new ArrayList<>();
        favlist.forEach(mapping -> {
            UserTable Fav_u = userService.getOneByEntityId(UserTable.ID(mapping.getFavUid()));
            ulist.add(UserSimpleInfo.builder()
                    .id(String.valueOf(Fav_u.getId()))
                    .avatar(Fav_u.getAvatar())
                    .signature(Fav_u.getSignature())
                    .build());
        });
        return BaseResult.success(UserListDTO.builder().data(ulist).build());
    }

    @AuthRequired
    @PostMapping("/updateinfo")
    public BaseResult<Null> updateUserInfo(@Nullable @RequestPart("avatar") MultipartFile avatar, HttpServletRequest req) {
        var token = req.getParameter("token");
        Long userId = Auth.getToken().getId();
        String fileUrl = null;


        if (avatar != null && avatar.getOriginalFilename() != null) {
            var fileName = avatar.getOriginalFilename();
            if (!fileName.endsWith(".jpg") && !fileName.endsWith(".png")) {
                return BaseResult.fail("File format error");
            }
            fileName = userId + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
            var res = fileServiceClient.uploadImage(avatar, fileName, FileBuckets.AVATAR_BUCKET.getBucketName(), token);

            if (res.getCode() != ResultCode.SUCCESS.getCode()) {
                return BaseResult.fail("File upload failed");
            }

            fileUrl = res.getData().getFileUrl();
        }
        var newUser = UserTable.builder().id(userId);


        var name = req.getParameter("name");
        var password = req.getParameter("password");
        var phone = req.getParameter("phone");
        var email = req.getParameter("email");
        var signature = req.getParameter("signature");
        var showCollection = req.getParameter("show_collection");

        if (password != null) {
            password = PasswordUtil.encrypt(password);
        }

        newUser.id(userId)
                .name(name)
                .password(password)
                .phone(phone)
                .email(email)
                .signature(signature)
                .showCollection(Boolean.parseBoolean(showCollection))
                .avatar(fileUrl)
                .updateTime(System.currentTimeMillis());

        userService.updateById(newUser.build());

        return BaseResult.success();
    }


}
