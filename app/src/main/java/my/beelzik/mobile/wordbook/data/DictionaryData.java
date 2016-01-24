package my.beelzik.mobile.wordbook.data;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by Andrey on 08.01.2016.
 */
@Accessors(prefix = "m")
public @Data
class DictionaryData implements Serializable{

    public enum WordType{
        NATIVE,
        LEARN
    }

    long mId;

    String mNative;

    String mLearn;

    int mNativeRating;

    int mLearnRating;

}
