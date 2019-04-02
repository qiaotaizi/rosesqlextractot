package com.jaiz.utils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

class ClipboardUtil {

    /**
     * 拷贝结果至剪贴板
     * @param text
     */
    public static void copyToClipBoard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text),null);
        System.out.println("**结果已拷贝至剪贴板,感谢使用!**");
    }

}
