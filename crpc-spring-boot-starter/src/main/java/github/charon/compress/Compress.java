package github.charon.compress;

import github.charon.common.extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] bytes);

    byte[] decompress(byte[] bytes);
}
