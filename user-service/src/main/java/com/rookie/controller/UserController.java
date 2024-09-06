package com.rookie.controller;

import com.mybatisflex.core.query.QueryWrapper;
import com.rookie.annotation.AuthRequired;
import com.rookie.aspect.Auth;
import com.rookie.model.Token;
import com.rookie.model.dto.UserInfoDTO;
import com.rookie.model.dto.UserLoginDTO;
import com.rookie.model.dto.UserSelfInfoDTO;
import com.rookie.model.entity.UserTable;
import com.rookie.model.result.BaseResult;
import com.rookie.model.result.None;
import com.rookie.service.UserService;
import com.rookie.utils.JWTUtils;
import com.rookie.utils.PasswordUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import static com.rookie.model.entity.table.UserTableTableDef.USER_TABLE;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expire}")
    private long expire;

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
        UserTable dbUser = null;

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
}
