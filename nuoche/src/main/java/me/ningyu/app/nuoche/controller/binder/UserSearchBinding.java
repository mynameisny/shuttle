package me.ningyu.app.nuoche.controller.binder;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.StringExpression;
import me.ningyu.app.nuoche.domain.QUser;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public class UserSearchBinding implements QuerydslBinderCustomizer
{

    @Override
    public void customize(QuerydslBindings bindings, EntityPath root)
    {
        QUser qUser = QUser.user;

        bindings.bind(qUser.code).first(StringExpression::eq);
        bindings.bind(qUser.userKey).first(StringExpression::eq);
        bindings.bind(qUser.name).first(StringExpression::contains);
    }

}