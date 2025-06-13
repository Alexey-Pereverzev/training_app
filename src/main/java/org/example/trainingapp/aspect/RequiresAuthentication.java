package org.example.trainingapp.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresAuthentication {
    Role[] allowedRoles() default {};       // list of roles allowed for the method
    boolean checkOwnership() default true;  // true if we want to check that the authorized user is the owner of the method
}
