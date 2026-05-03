package meugeninua.masterdetails.annotations;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import meugeninua.masterdetails.dto.ErrorDto;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "404", description = "Entity is not found")
public @interface ApiResponseNotFound {

    @AliasFor(annotation = ApiResponse.class, attribute = "content")
    Content content() default @Content(schema = @Schema(implementation = ErrorDto.class));
}
