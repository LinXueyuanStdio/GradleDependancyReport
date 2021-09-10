package com.timecat.plugin.report.analysis.ext;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/10
 * @description null
 * @usage null
 */
class LimitSizeConfig {

    static final long DEFAULT_LIB_SIZE_LIMIT = 1024 * 1024;
    static final long DEFAULT_FILE_SIZE_LIMIT = 100 * 1024;

    private long fileSize = DEFAULT_FILE_SIZE_LIMIT;
    private long libSize = DEFAULT_LIB_SIZE_LIMIT;

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getLibSize() {
        return libSize;
    }

    public void setLibSize(long libSize) {
        this.libSize = libSize;
    }
}
