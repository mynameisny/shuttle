package me.ningyu.app.locator.controller.binder;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringPath;
import me.ningyu.app.locator.entity.QPoint;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;

public class PointSearchBinding implements QuerydslBinderCustomizer
{
    @Override
    public void customize(@NotNull QuerydslBindings bindings, @NotNull EntityPath root)
    {
        QPoint qPoint = QPoint.point;
        bindings.bind(qPoint.longitude).first((path, value) -> path.eq(value));
        bindings.bind(qPoint.latitude).first((path, value) -> path.eq(value));
    }

    public Optional<Predicate> bind(StringPath path, Collection<? extends String> value)
    {
        if (value.size() == 1)
        {
            return Optional.of(path.eq(value.iterator().next()));
        }
        else
        {
            return Optional.of(path.in(value));
        }
    }
}
