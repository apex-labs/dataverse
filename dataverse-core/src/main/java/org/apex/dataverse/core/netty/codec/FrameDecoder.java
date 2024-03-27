package org.apex.dataverse.core.netty.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2022/11/8 20:22
 */
public class FrameDecoder extends LengthFieldBasedFrameDecoder {

    private final static Integer FIELD_LENGTH = 4;

    private final static Integer HEADER_LENGTH = 15;

    private final static Integer FRAME_MAX_LENGTH = 100 * 1024 * 1024;

    /**
     * 无参构造函数
     */
    public FrameDecoder() {
        super(FRAME_MAX_LENGTH, HEADER_LENGTH, FIELD_LENGTH, 0, 0);
    }

    public FrameDecoder(Integer maxLength) {
        super(maxLength, HEADER_LENGTH, FIELD_LENGTH, 0, 0);
    }

    public FrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    public FrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                        int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public FrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength,
                        int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    public FrameDecoder(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset,
                        int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip,
                        boolean failFast) {
        super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength,
                lengthAdjustment, initialBytesToStrip, failFast);
    }
}
