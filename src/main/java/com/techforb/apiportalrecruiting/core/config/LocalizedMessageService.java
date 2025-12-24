package com.techforb.apiportalrecruiting.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Servicio para la gestión de mensajes internacionalizados.
 * Proporciona métodos para obtener mensajes en el idioma especificado o en el idioma actual del contexto.
 */
@Component
@RequiredArgsConstructor
public class LocalizedMessageService {
    /**
     * Interfaz de Spring que permite la internacionalización.
     * Se usa para obtener mensajes localizados desde archivos de propiedades basados en el idioma del usuario.
     */
    private final MessageSource messageSource;

    /**
     * Obtiene un mensaje localizado a partir de un código de mensaje y un {@link Locale} específico.
     *
     * @param code  Código del mensaje en los archivos de recursos.
     * @param locale  Localización en la que se desea obtener el mensaje.
     * @return El mensaje localizado correspondiente al código proporcionado.
     */
    public String getMessage(String code, Locale locale) {
        return messageSource.getMessage(code, null, (locale != null) ? locale : LocaleContextHolder.getLocale());
    }

    /**
     * Obtiene un mensaje localizado a partir de un código de mensaje, usando la localización actual del contexto.
     *
     * @param code  Código del mensaje en los archivos de recursos.
     * @return El mensaje localizado correspondiente al código proporcionado en la localización actual.
     */
    public String getMessage(String code) {
        return getMessage(code, LocaleContextHolder.getLocale());
    }

    /**
     * Obtiene un mensaje localizado con parámetros dinámicos, usando la localización actual del contexto.
     *
     * @param code  Código del mensaje en los archivos de recursos.
     * @param args  Argumentos para el mensaje localizado.
     * @return El mensaje localizado con los parámetros insertados.
     */
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}