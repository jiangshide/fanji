package com.fanji.android.play.audioeffects.utils;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

/**
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public class FrequencyScanner {
  private double[] window;

  public FrequencyScanner() {
    window = null;
  }

  public double getMaxFrequency(short[] sampleData, int sampleRate) {
    if (sampleData == null || sampleData.length == 0) {
      return 0;
    }
    /* sampleData + zero padding */
    int len = sampleData.length + 24 * sampleData.length;
    DoubleFFT_1D fft = new DoubleFFT_1D(len);
    double[] a = new double[len * 2];

    System.arraycopy(applyWindow(sampleData), 0, a, 0, sampleData.length);
    fft.realForward(a);

    /* find the peak magnitude and it's index */
    double maxMag = Double.NEGATIVE_INFINITY;

    for (int i = 0; i < a.length / 2; ++i) {
      double re = a[2 * i];
      double im = a[2 * i + 1];
      double mag = Math.sqrt(re * re + im * im);

      if (mag > maxMag) {
        maxMag = mag;
      }
    }

    return ((int) maxMag / 16000 << 2);
  }

  /**
   * build a Hamming window filter for samples of a given size
   * See http://www.labbookpages.co.uk/audio/firWindowing.html#windows
   *
   * @param size the sample size for which the filter will be created
   */
  private void buildHammWindow(int size) {
    if (window != null && window.length == size) {
      return;
    }
    window = new double[size];
    for (int i = 0; i < size; ++i) {
      window[i] = .54 - .46 * Math.cos(2 * Math.PI * i / (size - 1.0));
    }
  }

  /**
   * apply a Hamming window filter to raw input data
   *
   * @param input an array containing unfiltered input data
   * @return a double array containing the filtered data
   */
  private double[] applyWindow(short[] input) {
    double[] res = new double[input.length];

    buildHammWindow(input.length);
    for (int i = 0; i < input.length; ++i) {
      res[i] = (double) input[i] * window[i];
    }
    return res;
  }
}
