package com.techforb.apiportalrecruiting.core.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;
/**
 * Configuración de resolución de locales e internacionalización de mensajes en la aplicación.
 */
@Configuration
public class LocalResolver  {
    /**
     * Define el {@link LocaleResolver} que se basa en el encabezado "Accept-Language" de la solicitud.
     * Establece el idioma predeterminado en inglés de EE.UU. (US).
     *
     * @return una instancia de {@link AcceptHeaderLocaleResolver} con el idioma predeterminado configurado.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }
    /**
     * Configura el {@link MessageSource} para la gestión de mensajes internacionalizados.
     * Utiliza archivos de propiedades con el prefijo "messages" y codificación UTF-8.
     *
     * @return una instancia de {@link ResourceBundleMessageSource} con las configuraciones establecidas.
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(3600);
        return messageSource;
    }
}
