package my.beelzik.mobile.wordbook.db.dao;

import java.util.List;
import java.util.Set;

import my.beelzik.mobile.wordbook.data.DictionaryData;
import my.beelzik.mobile.wordbook.db.WordDB;

/**
 * Created by Andrey on 08.01.2016.
 */
public interface DictionaryDAO {

    List<DictionaryData> getRandomWordList(int count,WordDB.DifferentType differentType, Set<Long> differentWordIdSet );
}
