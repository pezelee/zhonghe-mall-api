package mall.config.handler;

import mall.common.Constants;
import mall.common.ServiceResultEnum;
import mall.common.ZhongHeMallException;
import mall.config.annotation.TokenToAdminUser;
import mall.dao.ZhongHeAdminUserTokenMapper;
import mall.entity.AdminUserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class TokenToAdminUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final Logger logger = LoggerFactory.getLogger(TokenToAdminUserMethodArgumentResolver.class);

    @Autowired
    private ZhongHeAdminUserTokenMapper zhongHeAdminUserTokenMapper;

    public TokenToAdminUserMethodArgumentResolver() {
    }

    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(TokenToAdminUser.class)) {
            return true;
        }
        return false;
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (parameter.getParameterAnnotation(TokenToAdminUser.class) instanceof TokenToAdminUser) {
            String token = webRequest.getHeader("token");
//            logger.info("token：{}", token);
            if (null != token && !"".equals(token) && token.length() == Constants.TOKEN_LENGTH) {
                AdminUserToken adminUserToken = zhongHeAdminUserTokenMapper.selectByToken(token);
                if (adminUserToken == null) {
                    logger.info("管理员未登录：webRequest:{}, token：{}", webRequest.toString(),token);
                    ZhongHeMallException.fail(ServiceResultEnum.ADMIN_NOT_LOGIN_ERROR.getResult());
                } else if (adminUserToken.getExpireTime().getTime() <= System.currentTimeMillis()) {
                    logger.info("管理员登录过期：webRequest:{}, token：{}", webRequest.toString(),token);
                    ZhongHeMallException.fail(ServiceResultEnum.ADMIN_TOKEN_EXPIRE_ERROR.getResult());
                }
                return adminUserToken;
            } else {
                logger.info("管理员token错误：webRequest:{}, token：{}", webRequest.toString(),token);
                ZhongHeMallException.fail(ServiceResultEnum.ADMIN_NOT_LOGIN_ERROR.getResult());
            }
        }
        return null;
    }

}
