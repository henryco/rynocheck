package net.henryco.rynocheck.modules;

import com.github.henryco.injector.meta.annotations.Module;
import net.henryco.rynocheck.context.SimpleCommandContext;

/**
 * @author Henry on 15/01/18.
 */
@Module(components = {
		SimpleCommandContext.class
}) public final class ContextModule {
}