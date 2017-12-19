# 一些“跳过”按钮及缓冲框示例
### 前言
&emsp;&emsp;最近自定义控件的实践相对多一些，看到了别人app上实现的效果就想自己动手尝试下，看自己能不能做到。本文是对一些app第一个页面的“跳过”按钮及一些缓冲框的实现。一个控件就详细写一篇文，未免过于麻烦，所以这里是做了一个汇总，只写核心思路及相关伪代码，几个控件写成一篇。后面会给出完整代码。

#### 1. 矩形倒计时“跳过”
##### &emsp;&emsp;1. 先来看一下最终效果：

![image](https://github.com/yizhanzjz/ImageRepo/raw/master/progressSet0.gif)

##### &emsp;&emsp;2. 基本思路及相关代码
&emsp;&emsp;首先，我们看到“跳过”这两个字的背景是一个圆角矩形，而控件的形状一般是矩形，这时，我们把控件的背景设置成透明的，然后在控件上画出圆角矩形就可以了。
```
private int mBgColor = Color.TRANSPARENT;
// 画出控件背景色，为透明
canvas.drawColor(mBgColor)

//圆角矩形内填充的颜色，默认是半透明的黑色
private int mRectFillColor = 0x32000000;
//控件坐标系移至控件中心位置
canvas.translate(mWidth / 2, mHeight / 2);
//在其上画出圆角矩形，圆角大小由外界决定
mPaint.setColor(mRectFillColor);
RectF rectF = new RectF(-mWidth / 2, -mHeight / 2, mWidth / 2, mHeight / 2);
canvas.drawRoundRect(rectF, mCornerX, mCornerY, mPaint);
```
&emsp;&emsp;控件坐标系原点之所以移到控件的中心，是因为我们所画的内容基本上都是基于控件中心坐标的，移过去之后，我们更好确定我们所画内容的坐标。mBgColor、mRectFillColor、mCornerX、mCornerY都可以通过设定其set方法由控件外决定其值。

&emsp;&emsp;其次，“跳过”这两个字以及倒计时的数字，我们希望它们能够被视为一个整体，置于控件的中心。那我们要做的就是先计算出这两字以及倒计时数字的宽度。另外，注意，一般“跳过”俩字跟倒计时数字是有间距的。
```
private int mTextSize = 14;
//估算出字体的宽度
mPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * mTextSize);
mPaint.setTextAlign(Paint.Align.LEFT);

Rect rect = new Rect();
mPaint.getTextBounds(text, 0, text.length(), rect);
float textWidth = rect.right - rect.left;

private int mNumberTextSize = 12;
//估算出数字的宽度
mPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * mNumberTextSize);
mPaint.setTextAlign(Paint.Align.LEFT);

Rect rect1 = new Rect();
mPaint.getTextBounds("3", 0, 1, rect1);
float numberWidth = rect1.right - rect1.left;
```
&emsp;&emsp;计算“跳过”两个字宽度、计算倒计时数字宽度的方法是一样的，都是给画笔设置了textSize，然后用画笔的getTextBounds方法计算。算出了“跳过”两字和倒计时数字的宽度，如果两者有间距的话就能计算出这两者看做一个整体时，这个整体的宽度
```
//字体与数字之间的间距
private float mDivider = 15;
//总长度，中间添加间距
float length = textWidth + mDivider + numberWidth;
```
&emsp;&emsp;有了这个宽度就可以计算出，“跳出”和倒计时数字的基线坐标了:
```
mPaint.setTextAlign(Paint.Align.LEFT);
Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
“跳过”两字的基线坐标（jumpLoc）：(-length / 2,(-fontMetrics.top - fontMetrics.bottom) / 2)

mPaint.setTextAlign(Paint.Align.LEFT);
Paint.FontMetrics fontMetrics1 = mPaint.getFontMetrics();
倒计时数字的基线坐标（numberLoc）：(-length / 2 + textWidth + mDivider,(-fontMetrics1.top - fontMetrics1.bottom) / 2)
```
&emsp;&emsp;最后，算出了基线坐标就可以分别画出“跳出”和倒计时数字了：
```
mPaint.setColor(mTextColor);
mPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * mTextSize);
canvas.drawText(text, jumpLoc.x, jumpLoc.y, mPaint);

mPaint.setColor(mNumberTextColor);
mPaint.setTextSize(getResources().getDisplayMetrics().scaledDensity * mNumberTextSize);
canvas.drawText(mDelayTime + "", numberLoc.x, numberLoc.y, mPaint);
```
&emsp;&emsp;还有一点，标识倒计时数字的变量mDelayTime，本例是通过Timer完成倒计时。每过1秒，就向主线程发送一个消息，由主线程去控制mDelayTime的变化并重画控件（invalidate）。这部分代码看源码吧，在ThreeSecondJump0类中。

#### 2. 圆形倒计时“跳过”
##### &emsp;&emsp;1. 最终效果如下：

![image](https://github.com/yizhanzjz/ImageRepo/raw/master/progressSet1.gif)

##### &emsp;&emsp;2. 基本思路及相关代码
&emsp;&emsp;如上效果，是看了网易新闻的splash页做的。首先，还是需要平移控件坐标系，画透明背景，把画笔设置成填充模式画一个圆，这些跟上面控件类似，在这里就不重复说了。这里说下面几个点：画“跳过”两字、画动态变化的红色边界。
###### &emsp;&emsp;画“跳过”两字
&emsp;&emsp;仔细看“跳过”两个字，会发现它的大小基本上是跟圆边界顶着的，我们需要设置合适的大小才能够做到这样的效果，那怎么才能获取合适的大小呢？本例是通过循环得到的
```
do {
    mPaint.setTextSize(mTextSize);
    Rect rect = new Rect();
    mPaint.getTextBounds("跳过", 0, 2, rect);
    textWidth = rect.right - rect.left;
    textHeight = rect.bottom - rect.top;

    if (Math.pow(textWidth / 2, 2) + Math.pow(textHeight / 2, 2) > Math.pow(mCircleRadius, 2)) {
        mTextSize -= 0.5;
    } else {
        break;
    }

} while (true);
```
&emsp;&emsp;使textSize逐渐缩小，直至字体的宽一半的平方与字体高一半的平方之和不再大于圆半径的平方。仔细思考下，前后两者的值正好相等时，包含字体的矩形正好“顶着”外部的黑色圆边界。从循环中跳出之后就正好是顶着圆边界的textSize了，然后估算出基线坐标，画出“跳出”两个字就可以了
```
//字体的颜色
private int mTextColor = Color.WHITE;

mPaint.setColor(mTextColor);
Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
canvas.drawText("跳过", -textWidth / 2, (-fontMetrics.top - fontMetrics.bottom) / 2, mPaint);
```
###### &emsp;&emsp;画动态变化的红色边界
&emsp;&emsp;首先，画边界很简单，只需把画笔模式设置为stroke，半径与上述黑色圆半径一样，再设置上颜色和strokeWidth，基本就可以画出这个红色边界了
```
private int mCircleStrokeColor = Color.RED;

mPaint.setColor(mCircleStrokeColor);
mPaint.setStyle(Paint.Style.STROKE);
mPaint.setStrokeWidth(mStrokeWidth);
mPaint.setStrokeCap(Paint.Cap.ROUND);

Path path = new Path();
path.addCircle(0, 0, mCircleRadius, Path.Direction.CW);
canvas.drawPath(path, mPaint);
```
&emsp;&emsp;但上述代码只是能画出一个静态的红色边界，自己不会动的。我们还需要使用属性动画及PathMeasure使之动起来
```
...//省略上述Paint设置部分

Path path = new Path();
path.addCircle(0, 0, mCircleRadius, Path.Direction.CW);

//将此path交给一个PathMeasure对象
PathMeasure pathMeasure = new PathMeasure(path, false);
float length = pathMeasure.getLength();
Path pathDst = new Path();
//mCurrentValue，即为动画某一时刻的值
//getSegment可以获取此path的片段
pathMeasure.getSegment(mCurrentValue * length, length, pathDst, true);
canvas.drawPath(pathDst, mPaint);
```
&emsp;&emsp;上述就可以画出一个动态的红色边界了，但此边界的起点是在x轴的正半轴上的，我们希望边界起点在y轴的负半轴。也好解决，暂时将控件坐标系逆时针旋转90度，然后再画path，画完path之后再恢复控件坐标系即可
```
canvas.save();

canvas.rotate(-90);

...//省略的内容为上述画path部分

canvas.restore();
```
&emsp;&emsp;到此，起点在y轴负半轴、动态变化的红色边界就画完了。

#### 3. 仿ios菊花缓冲图标
##### &emsp;&emsp;最终效果图如下：

![image](https://github.com/yizhanzjz/ImageRepo/raw/master/progressSet2.gif)

##### &emsp;&emsp;基本思路及相关代码
&emsp;&emsp;模仿ios的缓冲图标来写的，但不知道ios那边具体是如何实现的。我的实现效果总感觉不那么好，不知道是不是因为图片的问题。下面是我的基本思路：

&emsp;&emsp;简单来讲，就是找了一张图片，然后给这张图片一个动画让它不停地旋转。那怎么使图片不停地旋转呢？本示例使用的Matrix的postRotate。下面一步一步来讲，一开始还是给控件画透明背景，将控件坐标系原点移到控件中心，这里不再多说。接下来就是画图片了，画图片有几个重载的方法，因为我们要用到矩阵的旋转，所以最终选择了如下这个方法：
```
public void drawBitmap(@NonNull Bitmap bitmap, @NonNull Matrix matrix, @Nullable Paint paint)
```
&emsp;&emsp;第一步，先从应用资源中获取所要旋转的图片
```
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading);
```
&emsp;&emsp;调用drawBitmap时，默认图片的左上角是跟坐标系原点重合的，而我们是想让图片居中显示，这就需要第二步，平移图片将图片中心与坐标系原点重合为一点。我们且看drawBitmap的第二个参数Matrix，此矩阵会作用于bitmap的坐标，使bitmap完成平移、旋转、缩放或者错切的操作。我们现在需要平移，需调用matrix的translate相关方法进行操作：
```
Matrix matrix = new Matrix();
//向x轴负半轴和y轴负半轴平移宽度的一半、高度的一半
matrix.postTranslate(-bitmapW / 2, -bitmapH / 2);
```
&emsp;&emsp;平移过后就可将图片的中心与坐标系的中心置于同一点了。平移之后，还会有一个问题：控件的宽高是由控件外设置的，而图片的大小是固定的，这就可能造成图片太大在控件内显示不全或图片太小不能够很好地显示在控件中。这就需要第三步，缩放图片。缩放到图片的较大边刚好跟控件的较小边重合，当然为了更好一些的显示效果，我们可以让这两个边留一些间距
```
//图片的宽高是一样的，所以只获取一个宽就可以了
int bitmapWH = bitmap.getWidth();
//找出控件宽高较小的
int temp = mWidth > mHeight ? mHeight : mWidth;
int tempContent = temp - mPadding;

//控件的较小边比图片的宽或高大多少倍
float s = tempContent * 1.0f / bitmapWH;

Matrix matrix = new Matrix();
//这是上一步说明过的平移
matrix.postTranslate(-bitmapWH / 2, -bitmapWH / 2);
matrix.postScale(s, s);
canvas.drawBitmap(bitmap, matrix, mPaint);
```
&emsp;&emsp;计算出当前控件的较小边是当前图片宽度或高度的多少倍，然后调用postScale等比例缩放，至此缩放完毕。以上的三步也只是画出了一个静态的图片并将其缩放到合适的宽高，而我们需要的是图片旋转起来。使图片旋转使用的就是matrix.postRotate这个方法，动起来就需要使用属性动画来不断改变图片旋转的角度来实现了：
```
//画图片
Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.loading);
//图片的宽高是一样的，所以只获取一个宽就可以了
int bitmapWH = bitmap.getWidth();
//找出控件宽高较小的
int temp = mWidth > mHeight ? mHeight : mWidth;
int tempContent = temp - mPadding;

//控件的较小边比图片的宽或高大多少倍
float s = tempContent * 1.0f / bitmapWH;

Matrix matrix = new Matrix();
matrix.postTranslate(-bitmapWH / 2, -bitmapWH / 2);
matrix.postScale(s, s);
//此处的mCurrentValue为属性动画0-1变化时某一个时刻的值
matrix.postRotate(360 * mCurrentValue);
canvas.drawBitmap(bitmap, matrix, mPaint);
```
&emsp;&emsp;至此，旋转缓冲控件完成。
#### 4. 仿交通银行缓冲框
&emsp;&emsp;手里有一张交通的卡，然后就下载了交通银行的app用，觉得他们的缓冲框挺有意思的，然后就想实现以下。

##### &emsp;&emsp;最终效果图如下

![image](https://github.com/yizhanzjz/ImageRepo/raw/master/progressSet3.gif)

##### &emsp;&emsp;基本思路及相关代码
&emsp;&emsp;基本思路大致是这样的：先使用Path画两个圆弧，使用某种方式让这两个圆弧的某一个点连接，然后为此Path创建一个PathMeasure对象，调用其getSegment方法获取此path的某一个片段，最后使用属性动画使此片段动起来。

&emsp;&emsp;首先，使用path画两个几乎为圆的圆弧，并保证第一个圆弧的最后一个点可以跟第二个圆弧的第一个点连接。而在画出第一个圆弧之前我们需要得到这些圆弧的半径
```
//计算出单个圆的半径
//当所画内容的宽尽量填充控件的宽时单个圆的半径
float radius0 = (mWidth - mPadding) * 1.0f / 4 - mStrokeWidth;
//当所画内容的高尽量填充控件的高时单个圆的半径
float radius1 = (mHeight - mPadding) * 1.0f / 2 - mStrokeWidth;
//较小值即为合适的半径
float radius = radius0 > radius1 ? radius1 : radius0;
```
&emsp;&emsp;说明下，mPadding为所画的内容控件边界的间距，mStrokeWidth为圆弧边界的宽度。上述代码的意思是，先计算出所画内容的宽高尽量填充控件宽高时的半径，为了使所画内容始终保持在控件内，选择这两个半径中的较小半径。接下来就是，画两个圆弧了：
```
//透明背景
canvas.drawColor(mBgColor);

//控件坐标系平移到控件中心
canvas.translate(mWidth / 2, mHeight / 2);

//画出背景轨道
mPaint.setColor(mCirCleBgColor);
mPaint.setStrokeWidth(mStrokeWidth);

Path path = new Path();

RectF rectF = new RectF(0, -radius, 2 * radius, radius);
//这里虽然可以设置成-360，但设置成-360就会有问题，多了一条线
path.addArc(rectF, 180, -359.99f);


RectF rectF1 = new RectF(-2 * radius, -radius, 0, radius);
path.arcTo(rectF1, 0, 359.99f);

canvas.drawPath(path, mPaint);
```
&emsp;&emsp;前两句代码是画出控件的透明背景、将控件坐标系的原点移至控件的中心。之后设置画笔的颜色及stroke宽度，接着是画两个圆弧，第一个圆弧是从180度开始画起，逆时针359.99度，结合包含此圆弧的矩形坐标可知，这个圆弧的起始点是坐标系原点，最后一个点是一个十分接近坐标系原点的点，而画第二个圆弧使用的是path.arcTo，这就能保证第二个圆弧的起始点与第一个圆弧的最后一个点连接（arcTo方法的特性）。第二个圆弧的起始点是坐标系原点，最后一个点是十分接近坐标系原点的点，并没有连接在一起。

&emsp;&emsp;这里有一个插曲，如果添加第一个圆弧到path时设置的不是-359.99f，而是-360f（划过的角度是可以设置成-360f的），就会莫名其妙是多一条直线。此直线并不是第二个圆弧的最后一个点与第一个圆弧起始点的连线（之所以这么想，是因为以为path自动闭合了），所以，就比较诡异，只能设置成一个十分靠近-360的值了。

&emsp;&emsp;其次，要做的是根据此path创建一个PathMeasure对象
```
//画动态的变化
//设置画笔的颜色
mPaint.setColor(mCircleColor);

//根据上述path创建PathMeasure
PathMeasure pathMeasure = new PathMeasure(path, false);
//pathMeasure可以计算出上述path的总长度
float length = pathMeasure.getLength();
//mCurrentValue为在0~1之间变化的属性动画某一刻的值
//startD，是获取的path片段的起始点，
//随着mCurrentValue的变化，startD从path的起始点变化到最后一点
float startD = mCurrentValue * length;
Path pathDst = new Path();
//ratio为动态线的长度
if (mCurrentValue + ratio <= 1) {
    float stopD = (mCurrentValue + ratio) * length;
    pathMeasure.getSegment(startD, stopD, pathDst, true);
} else {
    //先取出startD-length这个片段
    pathMeasure.getSegment(startD, length, pathDst, true);
    Path pathDst0 = new Path();
    //再从头取出不足mCurrentValue + ratio的片段
    pathMeasure.getSegment(0, (mCurrentValue + ratio - 1) * length, pathDst0, true);
    //都结合到path中
    pathDst.addPath(pathDst0);
}

canvas.drawPath(pathDst, mPaint);
```
&emsp;&emsp;通过getSegment获取path的片段，片段的起始点距离path起始点的距离startD为：mCurrentValue * length，大部分情况下，片段的最后一点距离path起始点的距离stopD为：(mCurrentValue + ratio) * length，这样写可使动态变化弧线的长度固定为ratio * length。而少部分情况下，mCurrentValue + ratio已经大于1，这时候就需要先取出start~length这个片段，然后再从path的起始点取出另一个片段，使两个片段的长度之后为ratio * length。

&emsp;&emsp;至此，仿交通银行的缓冲框已经制作完毕。

### 总结
&emsp;&emsp;两个“跳过”、两个缓冲框，实现起来大多都会用到属性动画完成动态效果，定时器本身也可以被动画替代。比较有挑战的，大概就是旋转图片时会用到矩阵的操作，完成一些动态效果时会用到PathMeasure来完成。其它可说的并没有太多，毕竟只是一些简单的效果。

&emsp;&emsp;这里是[完整代码](https://github.com/yizhanzjz/progresssets)
