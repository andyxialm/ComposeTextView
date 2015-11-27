package cn.refactor.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * 作者 : xialimin
 * 日期 : 15/11/20 10:16
 * 邮箱 : andyxialm@gmail.com
 * 描述 : 排版TextView, 可指定模板文本。
 */
public class ComposeTextView extends TextView {
    private static final int DEFAULT_HIGHLIGHT_COLOR = Color.RED;
    private static final char DEFAULT_SEPARATOR = ' ';

    private String mSeparator;
    private String mWholeText;
    private String mFormatString;

    private int mLastTextColor;
    private int mPerGroupLength;
    private int mLastTextLength;

    private boolean mIsLastStyleEnable;

    public ComposeTextView(Context context) {
        this(context, null);
    }

    public ComposeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComposeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ComposeTextView);
            mFormatString = a.getString(R.styleable.ComposeTextView_format);
            mLastTextColor = a.getColor(R.styleable.ComposeTextView_lastTextColor, DEFAULT_HIGHLIGHT_COLOR);
            mPerGroupLength = a.getInt(R.styleable.ComposeTextView_perGroupLength, 0);
            mLastTextLength = a.getInt(R.styleable.ComposeTextView_lastTextLength, 0);
            mIsLastStyleEnable = a.getBoolean(R.styleable.ComposeTextView_lastStyleEnable, false);
            a.recycle();
        }
    }

    /**
     * 设置格式，如"111 222 333 4444"
     * @param formatGrammar
     */
    public void setFormat(String formatGrammar) {
        mFormatString = formatGrammar;
    }

    /**
     * 设置后x位是否开启高亮和粗体
     * @param lastStyleEnable
     */
    public void setLastStyleEnable(boolean lastStyleEnable) {
        mIsLastStyleEnable = lastStyleEnable;
    }

    /**
     * 设置后x位高亮颜色
     * @param textColor
     */
    public void setLastTextColor(int textColor) {
        mLastTextColor = textColor;
    }

    @Override
    public CharSequence getText() {
        CharSequence text = super.getText();
        return hasSeparator() ? getTextIgnoreSeparator(mWholeText, mSeparator) :
                TextUtils.isEmpty(text) ? "" : text.toString().replaceAll(String.valueOf(DEFAULT_SEPARATOR), "");
    }

    private boolean hasSeparator() {
        return !TextUtils.isEmpty(mSeparator);
    }

    private CharSequence getTextIgnoreSeparator(CharSequence text, String separator) {
        if (getTextRuleEnable(text, separator)) {
            int startIndex = text.toString().indexOf(separator);
            int lastIndex = text.toString().lastIndexOf(separator);
            StringBuilder sb = new StringBuilder(text);
            sb = sb.deleteCharAt(startIndex);
            sb = sb.deleteCharAt(startIndex >= 0 ? --lastIndex : lastIndex);
            return sb.toString();
        }
        return "";
    }

    private boolean getTextRuleEnable(CharSequence text, String separator) {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(separator)) {
            return false;
        }
        int startIndex = text.toString().indexOf(separator);
        int lastIndex = text.toString().lastIndexOf(separator);
        if (startIndex < 0 || lastIndex < 0 || startIndex >= lastIndex) {
            return false;
        }
        return true;
    }

    public void setText(String text) {
        initText(text, null);
        if (TextUtils.isEmpty(text)) {
            super.setText(text);
            return;
        }
        String textString = getFormatString(mFormatString, text);
        if (mIsLastStyleEnable) {
            SpannableString ss = new SpannableString(textString);
            int lastIndexOf = ss.toString().lastIndexOf(String.valueOf(DEFAULT_SEPARATOR));
            if (lastIndexOf >= 0) {
                ss.setSpan(new ForegroundColorSpan(mLastTextColor), lastIndexOf, ss.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), lastIndexOf, ss.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                super.setText(ss, BufferType.SPANNABLE);
                return;
            }
        }
        super.setText(textString);
    }

    /**
     * 该重载方法适用于指定文本和非指定文本混合使用
     * sample: "NO: #111222333#, Checked."，则显示为"NO: 111 222 333, Checked."
     * @param text
     * @param separator
     */
    public void setText(String text, String separator) {
        initText(text, separator);
        if (TextUtils.isEmpty(mSeparator)) {
            this.setText(text);
            return;
        }
        if (TextUtils.isEmpty(text)) {
            super.setText(text);
            return;
        }

        setModeText(text, separator);
    }

    private void initText(String text, String separator) {
        mSeparator = separator;
        mWholeText = text;
    }

    /**
     * 设置每组字符的长度
     * sample: 123 456 789 0123  perGroupLength = 3, lastTextLength = 4
     * @param perGroupLength
     */
    public void setPerGroupLength(int perGroupLength) {
        mPerGroupLength = perGroupLength;
    }

    /**
     * 设置最后一组字符的长度
     * sample: 123 456 789 0123  perGroupLength = 3, lastTextLength = 4
     * @param lastTextLength
     */
    public void setLastTextLength(int lastTextLength) {
        mLastTextLength = lastTextLength;
    }

    /**
     * @param perGroupLength
     * @param lastTextLength
     */
    public void setGroupFormat(int perGroupLength, int lastTextLength) {
        this.setPerGroupLength(perGroupLength);
        this.setLastTextLength(lastTextLength);
    }

    //====================================Inner Methods====================================

    /**
     * 模式串文本setText
     * @param text
     * @param separator
     */
    private void setModeText(String text, String separator) {
        // 截取模式串
        int startIndex, endIndex;
        startIndex = text.indexOf(separator);
        endIndex = text.lastIndexOf(separator);
        if (startIndex >= endIndex) {
            super.setText(text);
            return;
        }
        String modeText = text.substring(startIndex + 1, endIndex);
        String frontText = text.substring(0, startIndex);
        String lastText  = text.substring(endIndex + 1);

        String textString = getFormatString(mFormatString, modeText);
        if (mIsLastStyleEnable) {
            SpannableString ss = new SpannableString(frontText + textString + lastText);
            int lastIndexOf = textString.toString().lastIndexOf(String.valueOf(DEFAULT_SEPARATOR));
            if (lastIndexOf >= 0) {
                ss.setSpan(new ForegroundColorSpan(mLastTextColor),
                        frontText.length() + lastIndexOf,
                        frontText.length() + textString.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                        frontText.length() + lastIndexOf,
                        frontText.length() + textString.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                super.setText(ss);
                return;
            }
        }
        super.setText(frontText + textString + lastText);
    }

    /**
     * 格式化
     * @param formatString
     * @param text
     * @return
     */
    private String getFormatString(String formatString, String text) {
        Integer[] indexArray = getIndexArray(formatString, text);
        return format(indexArray, text);
    }

    /**
     * 获得格式化后的字符串
     * @param indexArray
     * @param text
     * @return
     */
    private String format(Integer[] indexArray, String text) {
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < indexArray.length; i ++) {
            if (isPerGroupMode() && (indexArray[i] > text.length() + indexArray.length)) {
                return sb.toString();
            }
            if (!isPerGroupMode() && (indexArray[i] > text.length())) {
                return sb.toString();
            }

            sb = sb.insert(indexArray[i], String.valueOf(DEFAULT_SEPARATOR));
            if (isPerGroupMode() && i != (indexArray.length - 1)) {
                for (int index = i + 1; index < indexArray.length; index ++) {
                    indexArray[index] ++;
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获得需要插入分隔符的索引集合
     * @param formatString
     * @param text
     * @return
     */
    private Integer[] getIndexArray(String formatString, String text) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        if (isPerGroupMode()) {
            setIndexArrayByPerGroup(list, text);         // perGroup mode
        } else {
            setIndexArrayByGrammer(list, formatString);  // formatGrammer mode
        }
        return list.toArray(new Integer[list.size()]);
    }

    /**
     * formatString 模式，获得需要插入分隔符的索引集合
     * @param list
     * @param formatString
     */
    private void setIndexArrayByGrammer(ArrayList<Integer> list, String formatString) {
        if (TextUtils.isEmpty(formatString)) {
            return;
        }
        char[] chars = formatString.toCharArray();
        for (int i = 0; i < chars.length; i ++) {
            if (chars[i] == DEFAULT_SEPARATOR) {
                list.add(i);
            }
        }
    }

    /**
     * group 模式，获得需要插入分隔符的索引集合
     * @param list
     * @param text
     */
    private void setIndexArrayByPerGroup(ArrayList<Integer> list, String text) {
        if (mPerGroupLength == 0) {
            return;
        }
        if (mLastTextLength >= text.length()) {
            return;
        }
        if (mLastTextLength < text.length()) {
            int remain = (text.length() - mLastTextLength) % mPerGroupLength;
            if (remain > 0) {
                list.add(remain);
            }
            for (int i = remain + 1; i <= text.length() - mLastTextLength; i ++) {
                if ((i - remain) % mPerGroupLength == 0) {
                    list.add(i);
                }
            }

        }
    }

    /**
     * 是否是Group模式
     * @return
     */
    private boolean isPerGroupMode() {
        return mPerGroupLength > 0 && mLastTextLength > 0;
    }
}
