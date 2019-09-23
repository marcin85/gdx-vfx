/*******************************************************************************
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.filters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.crashinvaders.vfx.VfxRenderContext;
import com.crashinvaders.vfx.framebuffer.PingPongBuffer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.gl.VfxGLUtils;

public final class CombineFilter extends ShaderVfxFilter {

    private static final String U_TEXTURE0 = "u_texture0";
    private static final String U_TEXTURE1 = "u_texture1";
    private static final String U_SOURCE0_INTENSITY = "u_src0Intensity";
    private static final String U_SOURCE0_SATURATION = "u_src0Saturation";
    private static final String U_SOURCE1_INTENSITY = "u_src1Intensity";
    private static final String U_SOURCE1_SATURATION = "u_src1Saturation";

    private Texture secondTexture = null;
    private float s1i, s1s, s2i, s2s;

    public CombineFilter() {
        super(VfxGLUtils.compileShader(
                Gdx.files.classpath("shaders/screenspace.vert"),
                Gdx.files.classpath("shaders/combine.frag")));
        s1i = 1f;
        s2i = 1f;
        s1s = 1f;
        s2s = 1f;
        rebind();
    }

    @Override
    public void rebind() {
        super.rebind();
        program.begin();
        program.setUniformi(U_TEXTURE0, TEXTURE_HANDLE0);
        program.setUniformf(U_TEXTURE1, TEXTURE_HANDLE1);
        program.setUniformf(U_SOURCE0_INTENSITY, s1i);
        program.setUniformf(U_SOURCE1_INTENSITY, s2i);
        program.setUniformf(U_SOURCE0_SATURATION, s1s);
        program.setUniformf(U_SOURCE1_SATURATION, s2s);
        program.end();
    }

    @Override
    public void render(VfxRenderContext context, PingPongBuffer pingPongBuffer) {
        if (secondTexture == null) {
            throw new IllegalStateException("Second texture is not set. Use #setSecondInput() prior rendering.");
        }
        secondTexture.bind(TEXTURE_HANDLE1);

        super.render(context, pingPongBuffer);
    }

    public void setSecondInput(VfxFrameBuffer buffer) {
        if (buffer != null) {
            setSecondInput(buffer.getFbo().getColorBufferTexture());
        } else {
            setSecondInput((Texture) null);
        }
    }

    public void setSecondInput(Texture texture) {
        secondTexture = texture;
        if (texture != null) {
            setUniform(U_TEXTURE1, TEXTURE_HANDLE1);
        }
    }

    public float getSource1Intensity() {
        return s1i;
    }

    public void setSource1Intensity(float intensity) {
        s1i = intensity;
        setUniform(U_SOURCE0_INTENSITY, intensity);
    }

    public float getSource2Intensity() {
        return s2i;
    }

    public void setSource2Intensity(float intensity) {
        s2i = intensity;
        setUniform(U_SOURCE1_INTENSITY, intensity);
    }

    public float getSource1Saturation() {
        return s1s;
    }

    public void setSource1Saturation(float saturation) {
        s1s = saturation;
        setUniform(U_SOURCE0_SATURATION, saturation);
    }

    public float getSource2Saturation() {
        return s2s;
    }

    public void setSource2Saturation(float saturation) {
        s2s = saturation;
        setUniform(U_SOURCE1_SATURATION, saturation);
    }
}
