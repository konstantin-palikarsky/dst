package dst.ass2.service.facade.impl.providers.filter;


import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireAuth {
}
