package com.binggre.mmomail.config;

import com.binggre.binggreapi.objects.items.CustomItemStack;
import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmomail.MMOMail;
import lombok.Getter;

import java.io.File;

@Getter
public class GUIConfig {

    private static GUIConfig instance = null;

    public static GUIConfig getInstance() {
        if (instance == null) {
            instance = new GUIConfig();
        }
        return instance;
    }

    private String title;
    private int size;
    private int viewSize;

    private CustomItemStack prevItem;
    private CustomItemStack nextItem;
    private CustomItemStack mailItem;
    private CustomItemStack allReceiveItem;

    public void init() {
        File file = new File(MMOMail.getInstance().getDataFolder(), "gui.json");
        instance = FileManager.read(GUIConfig.class, file);
        instance.title = ColorManager.format(instance.title);
    }
}