package com.binggre.mmomail.config;

import com.binggre.binggreapi.utils.ColorManager;
import com.binggre.binggreapi.utils.file.FileManager;
import com.binggre.mmomail.MMOMail;
import com.binggre.mongolibraryplugin.MongoLibraryPlugin;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import org.bson.Document;

import java.lang.reflect.Field;

@Getter
public class MessageConfig {

    private static MessageConfig instance = null;

    public static MessageConfig getInstance() {
        if (instance == null) {
            instance = new MessageConfig();
        }
        return instance;
    }

    private String prefix = "[메일] ";
    private String shortOfMoney = "골드가 부족합니다.";
    private String inventoryCheck = "인벤토리 공간이 <size>칸 부족합니다.";
    private String inputLetter = "\n내용을 입력해 주세요.\n취소하려면 '취소' 또는 'cancel'\n ";
    private String inputGold = "\n금액을 입력해 주세요.\n취소하려면 '취소' 또는 'cancel'\n ";
    private String inputErrorNum = "숫자를 입력해 주세요";

    private String sendSuccess = "<player>님에게 메일을 전송했습니다.";
    private String sendNotFoundPlayer = "존재하지 않는 플레이어입니다.";
    private String sendOnlineAndNotLoad = "해당 플레이어는 로딩중입니다. 잠시 후 다시 시도해 주세요.";

    public void init() {
        MongoCollection<Document> collection = MongoLibraryPlugin.getInst()
                .getMongoClient()
                .getDatabase(MMOMail.DATA_BASE_NAME)
                .getCollection("Config-Message");

        Document configDocument = collection.find().first();

        if (configDocument == null) {
            configDocument = new Document();
            configDocument.put("prefix", prefix);
            configDocument.put("shortOfMoney", shortOfMoney);
            configDocument.put("inventoryCheck", inventoryCheck);
            configDocument.put("inputLetter", inputLetter);
            configDocument.put("inputGold", inputGold);
            configDocument.put("inputErrorNum", inputErrorNum);
            configDocument.put("sendSuccess", sendSuccess);
            configDocument.put("sendNotFoundPlayer", sendNotFoundPlayer);
            configDocument.put("sendOnlineAndNotLoad", sendOnlineAndNotLoad);
            collection.insertOne(configDocument);
        }

        instance = FileManager.toObject(configDocument.toJson(), MessageConfig.class);
        instance.prefix = ColorManager.format(instance.prefix);

        for (Field declaredField : instance.getClass().getDeclaredFields()) {
            if (declaredField.getType() == String.class) {
                try {
                    String str = declaredField.get(instance).toString();
                    if (str.equals(instance.prefix)) {
                        continue;
                    }
                    declaredField.setAccessible(true);
                    declaredField.set(instance, instance.prefix + ColorManager.format(str));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}