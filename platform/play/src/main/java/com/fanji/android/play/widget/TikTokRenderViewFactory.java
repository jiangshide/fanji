package com.fanji.android.play.widget;

import android.content.Context;

import com.fanji.android.play.dplay.render.IRenderView;
import com.fanji.android.play.dplay.render.RenderViewFactory;
import com.fanji.android.play.dplay.render.TextureRenderView;

public class TikTokRenderViewFactory extends RenderViewFactory {

    public static TikTokRenderViewFactory create() {
        return new TikTokRenderViewFactory();
    }

    @Override
    public IRenderView createRenderView(Context context) {
        return new TikTokRenderView(new TextureRenderView(context));
    }
}
