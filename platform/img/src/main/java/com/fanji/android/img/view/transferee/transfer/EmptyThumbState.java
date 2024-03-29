package com.fanji.android.img.view.transferee.transfer;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.fanji.android.img.view.image.TransferImage;
import com.fanji.android.img.view.transferee.loader.ImageLoader;
import com.fanji.android.img.view.transferee.style.IProgressIndicator;
import com.fanji.android.img.view.transferee.transfer.TransferAdapter;

import java.io.File;
import java.util.List;

/**
 * 高清图尚未加载，使用原 ImageView 中显示的图片作为缩略图。
 * 同时使用 {@link TransferImage#CATE_ANIMA_APART} 动画类型展示图片
 * created by jiangshide on 2020/4/20.
 * email:18311271399@163.com
 */
public class EmptyThumbState extends TransferState{
  EmptyThumbState(TransferLayout transfer) {
    super(transfer);
  }

  @Override
  public void prepareTransfer(final TransferImage transImage, final int position) {
    transImage.setImageDrawable(clipAndGetPlaceHolder(transImage, position));
  }

  @Override
  public TransferImage createTransferIn(final int position) {
    ImageView originImage = transfer.getTransConfig()
        .getOriginImageList().get(position);

    TransferImage transImage = createTransferImage(originImage);
    transImage.setImageDrawable(originImage.getDrawable());
    transImage.transformIn(TransferImage.STAGE_TRANSLATE);
    transfer.addView(transImage, 1);

    return transImage;
  }

  @Override
  public void transferLoad(final int position) {
    TransferAdapter adapter = transfer.transAdapter;
    final TransferConfig config = transfer.getTransConfig();
    final String imgUrl = config.getSourceImageList().get(position);
    final TransferImage targetImage = adapter.getImageItem(position);

    Drawable placeHolder;
    if (config.isJustLoadHitImage()) {
      // 如果用户设置了 JustLoadHitImage 属性，说明在 prepareTransfer 中已经
      // 对 TransferImage 裁剪过了， 所以只需要获取 Drawable 作为占位图即可
      placeHolder = getPlaceHolder(position);
    } else {
      placeHolder = clipAndGetPlaceHolder(targetImage, position);
    }

    final IProgressIndicator progressIndicator = config.getProgressIndicator();
    progressIndicator.attach(position, adapter.getParentItem(position));

    config.getImageLoader().showImage(imgUrl, targetImage,
        placeHolder, new ImageLoader.SourceCallback() {

          @Override
          public void onStart() {
            progressIndicator.onStart(position);
          }

          @Override
          public void onProgress(int progress) {
            progressIndicator.onProgress(position, progress);
          }

          @Override
          public void onDelivered(int status, File source) {
            progressIndicator.onFinish(position); // onFinish 只是说明下载完毕，并没更新图像
            switch (status) {
              case ImageLoader.STATUS_DISPLAY_SUCCESS: // 加载成功
                targetImage.transformIn(TransferImage.STAGE_SCALE);
                startPreview(targetImage, source, imgUrl, config, position);
                break;
              case ImageLoader.STATUS_DISPLAY_CANCEL:
                if (targetImage.getDrawable() != null) {
                  startPreview(targetImage, source, imgUrl, config, position);
                }
                break;
              case ImageLoader.STATUS_DISPLAY_FAILED:  // 加载失败，显示加载错误的占位图
                targetImage.setImageDrawable(config.getErrorDrawable(transfer.getContext()));
                break;
            }
          }
        });
  }

  @Override
  public TransferImage transferOut(final int position) {
    TransferImage transImage = null;

    TransferConfig config = transfer.getTransConfig();
    List<ImageView> originImageList = config.getOriginImageList();

    if (position <= originImageList.size() - 1 && originImageList.get(position) != null) {
      transImage = createTransferImage(originImageList.get(position));
      Drawable thumbnailDrawable = transfer.transAdapter.getImageItem(
          config.getNowThumbnailIndex()).getDrawable();
      transImage.setImageDrawable(thumbnailDrawable);
      transImage.transformOut(TransferImage.STAGE_TRANSLATE);

      transfer.addView(transImage, 1);
    }

    return transImage;
  }

  /**
   * 获取 position 位置处的 占位图，如果 position 超出下标，获取 MissDrawable
   *
   * @param position 图片索引
   * @return 占位图
   */
  private Drawable getPlaceHolder(int position) {
    Drawable placeHolder = null;

    TransferConfig config = transfer.getTransConfig();
    ImageView originImage = config.getOriginImageList().get(position);
    if (originImage != null) {
      placeHolder = originImage.getDrawable();
    }
    if (placeHolder == null) {
      placeHolder = config.getMissDrawable(transfer.getContext());
    }
    return placeHolder;
  }

  /**
   * 裁剪用于显示 PlachHolder 的 TransferImage
   *
   * @param targetImage 被裁剪的 TransferImage
   * @param position    图片索引
   * @return 被裁减的 TransferImage 中显示的 Drawable
   */
  private Drawable clipAndGetPlaceHolder(TransferImage targetImage, int position) {
    TransferConfig config = transfer.getTransConfig();

    Drawable placeHolder = getPlaceHolder(position);
    int[] clipSize = new int[2];
    ImageView originImage = config.getOriginImageList().get(position);
    if (originImage != null) {
      clipSize[0] = originImage.getWidth();
      clipSize[1] = originImage.getHeight();
    }

    clipTargetImage(targetImage, placeHolder, clipSize);
    return placeHolder;
  }
}
