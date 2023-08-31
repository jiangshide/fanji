package com.fanji.android.screenshort;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.fanji.android.screenshort.views.DoodleView;
import com.fanji.android.screenshort.views.DoodleView.MODE;
import com.fanji.android.util.LogUtil;

import java.util.HashMap;

/**
 * Time:2021/11/04 下午3:40
 * Author:Jack
 * Email:jiangshide@jingos.com
 * Description: Jingos
 */
public class DoodleMenu {
    private Context mContext;
    private DoodleView mDoodleView;

    private PopupWindow mPopupWindow;

    private HashMap<String, ImageView> images = new HashMap<>();

    private ImageView imgSmall, imgMiddle, imgBig;
    private ImageView redColor, blueColor, greenColor, yellowColor, pinkColor, whiteColor;

    private MODE modelType = MODE.RECTANGLE;
    private MODEL_SIZE modelSize = MODEL_SIZE.SMALL;

    private HashMap<String, String> defaultTxtSizeTag = new HashMap<>();
    private HashMap<String, Integer> defaultSelectedTxtSize = new HashMap<>();

    private HashMap<String, String> defaultRoundSizeTag = new HashMap<>();
    private HashMap<String, Integer> defaultRoundResourceColor = new HashMap<>();
    private HashMap<Integer, Integer> defaultSelectedRoundSize = new HashMap<>();

    private HashMap<String, String> defaultImageTag = new HashMap<>();
    private HashMap<String, Integer> defaultResourceColor = new HashMap<>();
    private HashMap<Integer, Integer> defaultSelectedColor = new HashMap<>();

    private HashMap<MODE, String> modelsTypes = new HashMap<>();

    enum MODEL_SIZE {
        SMALL, MIDDLE, BIG
    }

    public DoodleMenu(Context context, DoodleView doodleView) {
        this.mContext = context;
        this.mDoodleView = doodleView;
        modelsTypes.put(MODE.RECTANGLE, "rectangle");
        modelsTypes.put(MODE.ROUND, "round");
        modelsTypes.put(MODE.ARROW, "arrow");
        modelsTypes.put(MODE.PEN, "pen");
        modelsTypes.put(MODE.TEXT, "text");
        modelsTypes.put(MODE.MOSAIC, "mosaic");
        modelsTypes.put(MODE.REVERT, "revert");
        modelsTypes.put(MODE.RECOVER, "recover");
        modelsTypes.put(MODE.SHARE, "share");
    }

    public void popuWindow(View view, int layout, MODE type, int x) {
        if (mContext == null) return;
        View menus = LayoutInflater.from(mContext).inflate(layout, null);
        mPopupWindow = new PopupWindow(menus, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAsDropDown(view, x, 0);
        this.modelType = type;
        mDoodleView.setEditable(true);
        images.clear();
        if (type == MODE.RECTANGLE || type == MODE.ROUND || type == MODE.ARROW) {
            initTxt(menus);
            initColor(menus);
            mDoodleView.setMode(MODE.GRAPH_MODE);
        } else if (type == MODE.PEN || type == MODE.TEXT) {
            initTxt(menus);
            initColor(menus);
            mDoodleView.setMode(MODE.DOODLE_MODE);
        } else if (type == MODE.MOSAIC) {
            initTxt(menus);
            mDoodleView.setMode(MODE.MOSAIC_MODE);
        }
        defaultColor();
        defaultTxtSize();
        defaultRoundSize();
    }

    private void defaultTxtSize() {
        String tag = defaultTxtSizeTag.get(modelsTypes.get(modelType));
        LogUtil.e("---------tag:", tag);
        if (!TextUtils.isEmpty(tag)) {
            int size = defaultSelectedTxtSize.get(tag);
            LogUtil.e("------size:", size);
            ImageView imageView = images.get(tag);
            setSelectedText(imageView, size, false);
        }
    }

    private void defaultRoundSize() {
        String tag = defaultRoundSizeTag.get(modelsTypes.get(modelType));
        if (!TextUtils.isEmpty(tag)) {
            int resource = defaultRoundResourceColor.get(tag);
            int size = defaultSelectedRoundSize.get(resource);
            ImageView imageView = images.get(tag);
            setSelectedRound(imageView, resource, size, false);
        }
    }

    private void defaultColor() {
        String tag = defaultImageTag.get(modelsTypes.get(modelType));
        if (!TextUtils.isEmpty(tag)) {
            int resource = defaultResourceColor.get(tag);
            int color = defaultSelectedColor.get(resource);
            ImageView imageView = images.get(tag);
            setSelectedColor(imageView, resource, color, false);
        }
    }

    private void initTxt(View view) {
        imgSmall = view.findViewById(R.id.imgSmall);
        imgMiddle = view.findViewById(R.id.imgMiddle);
        imgBig = view.findViewById(R.id.imgBig);
        setCacheTxtView();
        imgSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelType == MODE.TEXT) {
                    setSelectedText(imgSmall, 20, true);
                } else {
                    setSelectedRound(imgSmall, R.drawable.cycle_frame_small, 3, true);
                }
            }
        });
        imgMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.e("-----modelType:", modelType);
                if (modelType == MODE.TEXT) {
                    setSelectedText(imgMiddle, 60, true);
                } else {
                    setSelectedRound(imgMiddle, R.drawable.cycle_frame_middle, 8, true);
                }
            }
        });
        imgBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (modelType == MODE.TEXT) {
                    setSelectedText(imgBig, 100, true);
                } else {
                    setSelectedRound(imgBig, R.drawable.cycle_frame_big, 13, true);
                }
            }
        });
    }

    private void setCacheTxtView() {
        images.put(imgSmall.getTag().toString(), imgSmall);
        images.put(imgMiddle.getTag().toString(), imgMiddle);
        images.put(imgBig.getTag().toString(), imgBig);
    }

    private void initColor(View view) {
        redColor = view.findViewById(R.id.redColor);
        blueColor = view.findViewById(R.id.blueColor);
        greenColor = view.findViewById(R.id.greenColor);
        yellowColor = view.findViewById(R.id.yellowColor);
        pinkColor = view.findViewById(R.id.pinkColor);
        whiteColor = view.findViewById(R.id.whiteColor);
        setCacheColorView();
        redColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(redColor, R.drawable.rectangle_frame_red, R.color.red, true);
            }
        });
        blueColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(blueColor, R.drawable.rectangle_frame_blue, R.color.blue, true);
            }
        });
        greenColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(greenColor, R.drawable.rectangle_frame_green, R.color.green, true);
            }
        });
        yellowColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(yellowColor, R.drawable.rectangle_frame_yellow, R.color.yellow, true);
            }
        });
        pinkColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(pinkColor, R.drawable.rectangle_frame_pink, R.color.pink, true);
            }
        });
        whiteColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedColor(whiteColor, R.drawable.rectangle_frame_white, R.color.white, true);
            }
        });
    }

    private void setCacheColorView() {
        images.put(redColor.getTag().toString(), redColor);
        images.put(blueColor.getTag().toString(), blueColor);
        images.put(greenColor.getTag().toString(), greenColor);
        images.put(yellowColor.getTag().toString(), yellowColor);
        images.put(pinkColor.getTag().toString(), pinkColor);
        images.put(whiteColor.getTag().toString(), whiteColor);
    }

    private void setSelectedText(ImageView imageView, int size, boolean isCancel) {
        imgSmall.setImageResource(R.mipmap.text_small);
        imgMiddle.setImageResource(R.mipmap.text_middle);
        imgBig.setImageResource(R.mipmap.text_big);
        imgSmall.setBackgroundResource(R.drawable.rectangle_frame_text);
        imgMiddle.setBackgroundResource(R.drawable.rectangle_frame_text);
        imgBig.setBackgroundResource(R.drawable.rectangle_frame_text);
        imageView.setBackgroundResource(R.drawable.white_round);
        mDoodleView.setPaintSize(size);
        defaultTxtSizeTag.put(modelsTypes.get(modelType), imageView.getTag().toString());
        LogUtil.e("setSelectText~defaultTxtSizeTag:", defaultTxtSizeTag.toString());
        defaultSelectedTxtSize.put(imageView.getTag().toString(), size);
        if (isCancel) {
            cancel();
        }
    }

    private void setSelectedRound(ImageView imageView, int drawableColor, int size, boolean isCancel) {
        if (modelType == MODE.MOSAIC) {
            size *= 2;
        }
        imgSmall.setBackgroundResource(R.drawable.translation);
        imgMiddle.setBackgroundResource(R.drawable.translation);
        imgBig.setBackgroundResource(R.drawable.translation);
        imgSmall.setImageResource(R.drawable.cycle_small);
        imgMiddle.setImageResource(R.drawable.cycle_middle);
        imgBig.setImageResource(R.drawable.cycle_big);
        imageView.setImageResource(drawableColor);
        mDoodleView.setPaintWidth(size);
        defaultRoundSizeTag.put(modelsTypes.get(modelType), imageView.getTag().toString());
        defaultRoundResourceColor.put(imageView.getTag().toString(), drawableColor);
        defaultSelectedRoundSize.put(drawableColor, size);
        if (isCancel) {
            cancel();
        }
    }

    private void setSelectedColor(ImageView imageView, int drawableColor, int color, boolean isCancel) {
        if (mContext == null) return;
        redColor.setImageResource(R.drawable.rectangle_red);
        blueColor.setImageResource(R.drawable.rectangle_blue);
        greenColor.setImageResource(R.drawable.rectangle_green);
        yellowColor.setImageResource(R.drawable.rectangle_yellow);
        pinkColor.setImageResource(R.drawable.rectangle_pink);
        whiteColor.setImageResource(R.drawable.rectangle_white);
        imageView.setImageResource(drawableColor);
        setDoodlePaintColor(mContext.getResources().getColor(color));
        defaultImageTag.put(modelsTypes.get(modelType), imageView.getTag().toString());
        defaultResourceColor.put(imageView.getTag().toString(), drawableColor);
        defaultSelectedColor.put(drawableColor, color);
        String defaultSelectedColorStr = defaultSelectedColor.toString();
        if (isCancel) {
            cancel();
        }
    }

    private void setDoodlePaintColor(int color) {
        if (mDoodleView != null) {
            mDoodleView.setPaintColor(color);
        }
    }

    public void cancel() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }
}
