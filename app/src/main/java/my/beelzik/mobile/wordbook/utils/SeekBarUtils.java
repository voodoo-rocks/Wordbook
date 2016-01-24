package my.beelzik.mobile.wordbook.utils;

import android.widget.SeekBar;

/**
 * Created by Andrey on 14.01.2016.
 */
public class SeekBarUtils {

    public static  void setCurrentSeekBarProgress(SeekBar seekBar, int realScaleMin, int realScaleMax, int current){

        int seekBarMax = seekBar.getMax();

        int scaleDiff = realScaleMax - realScaleMin;

        int step = scaleDiff / seekBarMax;

        int currentSeekBarProgress = (current - realScaleMin) / step ;

        BeeLog.debug("currentSeekBarProgress: " + currentSeekBarProgress);
        seekBar.setProgress(currentSeekBarProgress);
    }

    public static  int getCurrentSeekBarProgress(SeekBar seekBar, int realScaleMin, int realScaleMax){

        int seekBarMax = seekBar.getMax();

        int scaleDiff = realScaleMax - realScaleMin;

        int step = scaleDiff / seekBarMax;

        return realScaleMin + seekBar.getProgress() * step;
    }
}
