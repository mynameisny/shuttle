package me.ningyu.app.nuoche.controller.binder;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.EnumExpression;
import com.querydsl.core.types.dsl.StringExpression;
import me.ningyu.app.nuoche.domain.QProvider;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

public class ProviderSearchBinding implements QuerydslBinderCustomizer
{

    @Override
    public void customize(QuerydslBindings bindings, EntityPath root)
    {
        QProvider qProvider = QProvider.provider;

        bindings.bind(qProvider.id).first(StringExpression::eq);
        bindings.bind(qProvider.vendor).first(EnumExpression::eq);
    }

}