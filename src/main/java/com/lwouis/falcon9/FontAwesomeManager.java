package com.lwouis.falcon9;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.lwouis.falcon9.injection.InjectLogger;

@Singleton
public class FontAwesomeManager {

  @InjectLogger
  private Logger logger;

  private GlyphFont fontAwesome;

  public FontAwesomeManager() {
    try {
      PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
      // dynamic path to be flexible with font-awesome maven artifact versions
      String ttfPath = "META-INF/resources/webjars/font-awesome/*/fonts/fontawesome-webfont.ttf";
      Resource[] resources = resolver
              .getResources("classpath*:" + ttfPath);
      // override the default online font loader with a local font loader
      GlyphFontRegistry.register(new FontAwesome(resources[0].getURL().toString()));
    }
    catch (Throwable t) {
      logger.error("Failed to load FontAwesome from local resource. Fallbacking to online file.", t);
    }
    fontAwesome = GlyphFontRegistry.font("FontAwesome");
  }

  public Glyph getGlyph(FontAwesome.Glyph glyph) {
    return fontAwesome.create(glyph);
  }
}
