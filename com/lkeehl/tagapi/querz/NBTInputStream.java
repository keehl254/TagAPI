package com.lkeehl.tagapi.querz;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NBTInputStream extends DataInputStream {

    private static final Map<Byte, ExceptionBiFunction<NBTInputStream, Integer, ? extends Tag<?>, IOException>> readers = new HashMap<>();

    static {
        readers.put((byte) 3, (i, d) -> readInt(i));
        readers.put((byte) 10, NBTInputStream::readCompound);
    }

    public NBTInputStream(InputStream in) {
        super(in);
    }

    public CompoundTag readTag(int maxDepth) throws IOException {
        byte id = readByte();
        readUTF();
        return (CompoundTag) readTag(id, maxDepth);
    }

    private Tag<?> readTag(byte type, int maxDepth) throws IOException {
        ExceptionBiFunction<NBTInputStream, Integer, ? extends Tag<?>, IOException> f;
        if ((f = readers.get(type)) == null) {
            throw new IOException("invalid tag id \"" + type + "\"");
        }
        return f.accept(this, maxDepth);
    }

    private static IntTag readInt(NBTInputStream in) throws IOException {
        return new IntTag(in.readInt());
    }

    private static CompoundTag readCompound(NBTInputStream in, int maxDepth) throws IOException {
        CompoundTag comp = new CompoundTag();
        for (int id = in.readByte() & 0xFF; id != 0; id = in.readByte() & 0xFF) {
            String key = in.readUTF();
            Tag<?> element = in.readTag((byte) id, in.decrementMaxDepth(maxDepth));
            comp.put(key, element);
        }
        return comp;
    }

    public int decrementMaxDepth(int maxDepth) {
        if (maxDepth < 0) {
            throw new IllegalArgumentException("negative maximum depth is not allowed");
        } else if (maxDepth == 0) {
            throw new RuntimeException("reached maximum depth of NBT structure");
        }
        return --maxDepth;
    }

    @FunctionalInterface
    public interface ExceptionBiFunction<T, U, R, E extends Exception> {
        R accept(T t, U u) throws E;
    }

    public static class IntTag extends Tag<Integer> {

        public IntTag(int value) {
            super(value);
        }

    }

}
