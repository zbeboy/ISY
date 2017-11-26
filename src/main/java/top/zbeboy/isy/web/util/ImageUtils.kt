package top.zbeboy.isy.web.util

import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.awt.*
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.*
import java.net.URL
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.ImageOutputStream

/**
 * Created by zbeboy 2017-11-26 .
 **/
open class ImageUtils {

    companion object {
        private val log = LoggerFactory.getLogger(ImageUtils::class.java)

        /**
         * 获取图片尺寸信息
         *
         * @param filePath a [java.lang.String] object.
         * @return [width, height]
         */
        @JvmStatic
        @Throws(Exception::class)
        fun getSizeInfo(filePath: String): IntArray {
            val file = File(filePath)
            return getSizeInfo(file)
        }

        /**
         * 获取图片尺寸信息
         *
         * @param url a [java.net.URL] object.
         * @return [width, height]
         */
        @JvmStatic
        @Throws(Exception::class)
        fun getSizeInfo(url: URL): IntArray {
            val input: InputStream
            try {
                input = url.openStream()
                return getSizeInfo(input)
            } catch (e: IOException) {
                log.error("Get file size error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 获取图片尺寸信息
         *
         * @param file a [java.io.File] object.
         * @return [width, height]
         */
        @JvmStatic
        @Throws(Exception::class)
        fun getSizeInfo(file: File): IntArray {
            if (!file.exists()) {
                throw Exception("file " + file.absolutePath + " doesn't exist.")
            }
            val input: BufferedInputStream
            try {
                input = BufferedInputStream(FileInputStream(file))
                return getSizeInfo(input)
            } catch (e: FileNotFoundException) {
                log.error("Get file size error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 获取图片尺寸
         *
         * @param input a [java.io.InputStream] object.
         * @return [width, height]
         */
        @JvmStatic
        @Throws(Exception::class)
        fun getSizeInfo(input: InputStream?): IntArray {
            try {
                val img = ImageIO.read(input!!)
                val w = img.getWidth(null)
                val h = img.getHeight(null)
                return intArrayOf(w, h)
            } catch (e: IOException) {
                log.error("Get file size error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 重调图片尺寸
         *
         * @param srcFilePath 原图路径
         * @param destFile    目标文件
         * @param width       新的宽度，小于1则忽略，按原图比例缩放
         * @param height      新的高度，小于1则忽略，按原图比例缩放
         */
        @JvmStatic
        @Throws(Exception::class)
        fun resize(srcFilePath: String, destFile: String, width: Int, height: Int) {
            resize(srcFilePath, destFile, width, height, -1, -1)
        }

        /**
         * 重调图片尺寸
         *
         * @param input  a [java.io.InputStream] object.
         * @param output a [java.io.OutputStream] object.
         * @param width  a int.
         * @param height a int.
         */
        @JvmStatic
        @Throws(Exception::class)
        fun resize(input: InputStream, output: OutputStream, width: Int, height: Int) {
            resize(input, output, width, height, -1, -1)
        }

        /**
         * 重调图片尺寸
         *
         * @param input     a [java.io.InputStream] object.
         * @param output    a [java.io.OutputStream] object.
         * @param width     a int.
         * @param height    a int.
         * @param maxWidth  a int.
         * @param maxHeight a int.
         */
        @JvmStatic
        @Throws(Exception::class)
        fun resize(input: InputStream, output: OutputStream,
                   width: Int, height: Int, maxWidth: Int, maxHeight: Int) {

            if (width < 1 && height < 1 && maxWidth < 1 && maxHeight < 1) {
                try {
                    IOUtils.copy(input, output)
                } catch (e: IOException) {
                    throw Exception("resize error: ", e)
                }

            }
            try {
                val img = ImageIO.read(input)
                val hasNotAlpha = !img.colorModel.hasAlpha()
                val w = img.getWidth(null).toDouble()
                val h = img.getHeight(null).toDouble()
                var toWidth: Int
                var toHeight: Int
                var rate = w / h

                if (width > 0 && height > 0) {
                    rate = width.toDouble() / height.toDouble()
                    toWidth = width
                    toHeight = height
                } else if (width > 0) {
                    toWidth = width
                    toHeight = (toWidth / rate).toInt()
                } else if (height > 0) {
                    toHeight = height
                    toWidth = (toHeight * rate).toInt()
                } else {
                    toWidth = (w as Number).toInt()
                    toHeight = (h as Number).toInt()
                }

                if (maxWidth > 0 && toWidth > maxWidth) {
                    toWidth = maxWidth
                    toHeight = (toWidth / rate).toInt()
                }
                if (maxHeight > 0 && toHeight > maxHeight) {
                    toHeight = maxHeight
                    toWidth = (toHeight * rate).toInt()
                }

                val tag = BufferedImage(toWidth, toHeight, if (hasNotAlpha) BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB)

                // Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
                tag.graphics.drawImage(img.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH), 0, 0, null)
                ImageIO.write(tag, if (hasNotAlpha) "jpg" else "png", output)
            } catch (e: Exception) {
                log.error("Resize error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 重调图片尺寸
         *
         * @param srcFile   原图路径
         * @param destFile  目标文件
         * @param width     新的宽度，小于1则忽略，按原图比例缩放
         * @param height    新的高度，小于1则忽略，按原图比例缩放
         * @param maxWidth  最大宽度，限制目标图片宽度，小于1则忽略此设置
         * @param maxHeight 最大高度，限制目标图片高度，小于1则忽略此设置
         */
        @JvmStatic
        @Throws(Exception::class)
        fun resize(srcFile: String, destFile: String, width: Int,
                   height: Int, maxWidth: Int, maxHeight: Int) {
            resize(File(srcFile), File(destFile), width, height, maxWidth, maxHeight)
        }

        /**
         * 重调图片尺寸
         *
         * @param srcFile  原图路径
         * @param destFile 目标文件
         * @param width    新的宽度，小于1则忽略，按原图比例缩放
         * @param height   新的高度，小于1则忽略，按原图比例缩放
         */
        @JvmStatic
        @Throws(Exception::class)
        fun resize(srcFile: File, destFile: File, width: Int, height: Int) {
            resize(srcFile, destFile, width, height, -1, -1)
        }

        /**
         * 重调图片尺寸
         *
         * @param srcFile   原图路径
         * @param destFile  目标文件
         * @param width     新的宽度，小于1则忽略，按原图比例缩放
         * @param height    新的高度，小于1则忽略，按原图比例缩放
         * @param maxWidth  最大宽度，限制目标图片宽度，小于1则忽略此设置
         * @param maxHeight 最大高度，限制目标图片高度，小于1则忽略此设置
         */
        @JvmStatic
        @Throws(Exception::class)
        fun resize(srcFile: File, destFile: File, width: Int,
                   height: Int, maxWidth: Int, maxHeight: Int) {
            if (destFile.exists()) {
                destFile.delete()
            } else {
                destFile.parentFile.mkdirs()
            }
            val input: InputStream
            val output: OutputStream
            try {
                input = BufferedInputStream(FileInputStream(srcFile))
                output = FileOutputStream(destFile)
                resize(input, output, width, height, maxWidth, maxHeight)
            } catch (e: FileNotFoundException) {
                log.error("Resize error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 裁剪图片
         *
         * @param source a [java.lang.String] object.
         * @param target a [java.lang.String] object.
         * @param x      a int.
         * @param y      a int.
         * @param w      a int.
         * @param h      a int.
         */
        @JvmStatic
        @Throws(Exception::class)
        fun crop(source: String, target: String, x: Int, y: Int, w: Int, h: Int) {
            crop(File(source), File(target), x, y, w, h)
        }

        /**
         * 裁剪图片
         *
         * @param source a [java.io.File] object.
         * @param target a [java.io.File] object.
         * @param x      a int.
         * @param y      a int.
         * @param w      a int.
         * @param h      a int.
         */
        @JvmStatic
        @Throws(Exception::class)
        fun crop(source: File, target: File, x: Int, y: Int, w: Int, h: Int) {
            val output: OutputStream
            val input: InputStream
            val ext = FilenameUtils.getExtension(target.name)
            try {
                input = BufferedInputStream(FileInputStream(source))
                if (target.exists()) {
                    target.delete()
                } else {
                    target.parentFile.mkdirs()
                }
                output = BufferedOutputStream(FileOutputStream(target))
            } catch (e: IOException) {
                log.error("Crop error : {}", e)
                throw Exception(e)
            }

            crop(input, output, x, y, w, h, StringUtils.equalsIgnoreCase("png", ext))
        }

        /**
         * 裁剪图片
         *
         * @param x      a int.
         * @param y      a int.
         * @param w      a int.
         * @param h      a int.
         * @param input  a [java.io.InputStream] object.
         * @param output a [java.io.OutputStream] object.
         * @param isPNG  a boolean.
         */
        @JvmStatic
        @Throws(Exception::class)
        fun crop(input: InputStream, output: OutputStream, x: Int,
                 y: Int, w: Int, h: Int, isPNG: Boolean) {
            try {
                val srcImg = ImageIO.read(input)
                val tmpWidth = srcImg.width
                val tmpHeight = srcImg.height
                val xx = Math.min(tmpWidth - 1, x)
                val yy = Math.min(tmpHeight - 1, y)

                var ww = w
                if (xx + w > tmpWidth) {
                    ww = Math.max(1, tmpWidth - xx)
                }
                var hh = h
                if (yy + h > tmpHeight) {
                    hh = Math.max(1, tmpHeight - yy)
                }

                val dest = srcImg.getSubimage(xx, yy, ww, hh)

                val tag = BufferedImage(w, h, if (isPNG) BufferedImage.TYPE_INT_ARGB else BufferedImage.TYPE_INT_RGB)

                tag.graphics.drawImage(dest, 0, 0, null)
                ImageIO.write(tag, if (isPNG) "png" else "jpg", output)
            } catch (e: Exception) {
                log.error("Crop error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 压缩图片,PNG图片按JPG处理
         *
         * @param input   a [java.io.InputStream] object.
         * @param output  a [java.io.OutputStream] object.
         * @param quality 图片质量0-1之间
         */
        @JvmStatic
        @Throws(Exception::class)
        fun optimize(input: InputStream, output: OutputStream, quality: Float) {

            // create a BufferedImage as the result of decoding the supplied
            // InputStream
            val image: BufferedImage
            var ios: ImageOutputStream? = null
            var writer: ImageWriter? = null
            try {
                image = ImageIO.read(input)

                // get all image writers for JPG format
                val writers = ImageIO.getImageWritersByFormatName("jpeg")

                if (!writers.hasNext())
                    throw IllegalStateException("No writers found")

                writer = writers.next() as ImageWriter
                ios = ImageIO.createImageOutputStream(output)

                writer.output = ios

                val param = writer.defaultWriteParam

                // optimize to a given quality
                param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                param.compressionQuality = quality

                // appends a complete image stream containing a single image and
                // associated stream and image metadata and thumbnails to the output
                writer.write(null, IIOImage(image, null, null), param)
            } catch (e: IOException) {
                log.error("Optimize error : {}", e)
            } finally {
                if (ios != null) {
                    try {
                        ios.close()
                    } catch (e: IOException) {
                        log.error("ImageOutputStream closed error : {}", e)
                    }

                }
                if (writer != null) {
                    writer.dispose()
                }
            }
        }

        /**
         * 压缩图片
         *
         * @param source  a [java.lang.String] object.
         * @param target  a [java.lang.String] object.
         * @param quality a float.
         */
        @JvmStatic
        @Throws(Exception::class)
        fun optimize(source: String, target: String, quality: Float) {
            val fromFile = File(source)
            val toFile = File(target)
            optimize(fromFile, toFile, quality)
        }

        /**
         * 压缩图片
         *
         * @param source  a [java.io.File] object.
         * @param target  a [java.io.File] object.
         * @param quality 图片质量0-1之间
         */
        @JvmStatic
        @Throws(Exception::class)
        fun optimize(source: File, target: File, quality: Float) {
            if (target.exists()) {
                target.delete()
            } else {
                target.parentFile.mkdirs()
            }
            val `is`: InputStream
            val os: OutputStream
            try {
                `is` = BufferedInputStream(FileInputStream(source))
                os = BufferedOutputStream(FileOutputStream(target))
                optimize(`is`, os, quality)
            } catch (e: FileNotFoundException) {
                log.error("Optimize error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 制作圆角
         *
         * @param srcFile      原文件
         * @param destFile     目标文件
         * @param cornerRadius 角度
         */
        @JvmStatic
        @Throws(Exception::class)
        fun makeRoundedCorner(srcFile: File, destFile: File, cornerRadius: Int) {
            val `in`: InputStream
            val out: OutputStream

            try {
                `in` = BufferedInputStream(FileInputStream(srcFile))
                destFile.parentFile.mkdirs()
                out = BufferedOutputStream(FileOutputStream(destFile))
                makeRoundedCorner(`in`, out, cornerRadius)
            } catch (e: IOException) {
                log.error("MakeRoundedCorner error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 制作圆角
         *
         * @param srcFile      原文件
         * @param destFile     目标文件
         * @param cornerRadius 角度
         */
        @JvmStatic
        @Throws(Exception::class)
        fun makeRoundedCorner(srcFile: String, destFile: String, cornerRadius: Int) {
            makeRoundedCorner(File(srcFile), File(destFile), cornerRadius)
        }

        /**
         * 制作圆角
         *
         * @param inputStream  原图输入流
         * @param outputStream 目标输出流
         * @param radius       角度
         */
        @JvmStatic
        @Throws(Exception::class)
        fun makeRoundedCorner(inputStream: InputStream,
                              outputStream: OutputStream, radius: Int) {
            val sourceImage: BufferedImage
            val targetImage: BufferedImage
            try {
                sourceImage = ImageIO.read(inputStream)
                val w = sourceImage.width
                val h = sourceImage.height
                println(w)

                val cornerRadius = if (radius < 1) w / 4 else radius

                targetImage = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)

                val g2 = targetImage.createGraphics()

                // This is what we want, but it only does hard-clipping, i.e.
                // aliasing
                // g2.setClip(new RoundRectangle2D ...)

                // so instead fake soft-clipping by first drawing the desired clip
                // shape
                // in fully opaque white with antialiasing enabled...
                g2.composite = AlphaComposite.Src
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.color = Color.WHITE
                g2.fill(RoundRectangle2D.Float(0f, 0f, w.toFloat(), h.toFloat(), cornerRadius.toFloat(), cornerRadius.toFloat()))

                // ... then compositing the image on top,
                // using the white shape from above as alpha source
                g2.composite = AlphaComposite.SrcAtop
                g2.drawImage(sourceImage, 0, 0, null)
                g2.dispose()
                ImageIO.write(targetImage, "png", outputStream)
            } catch (e: IOException) {
                log.error("MakeRoundedCorner error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 图片旋转
         *
         * @param srcFile  源文文件
         * @param destFile 输出文件
         * @param angle    rotate
         * @throws Exception 异常
         */
        @JvmStatic
        @Throws(Exception::class)
        fun makeRotate(srcFile: File, destFile: File, angle: Int) {
            val `in`: InputStream
            val out: OutputStream

            try {
                `in` = BufferedInputStream(FileInputStream(srcFile))
                destFile.parentFile.mkdirs()
                out = BufferedOutputStream(FileOutputStream(destFile))
                makeRotate(`in`, out, angle)
            } catch (e: IOException) {
                log.error("makeRotate error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 图片旋转
         *
         * @param srcFile  源文文件
         * @param destFile 输出文件
         * @param angle    rotate
         * @throws Exception 异常
         */
        @JvmStatic
        @Throws(Exception::class)
        fun makeRotate(srcFile: String, destFile: String, angle: Int) {
            makeRotate(File(srcFile), File(destFile), angle)
        }

        /**
         * 图片旋转
         *
         * @param inputStream  输入流
         * @param outputStream 输出流
         * @param angle        rotate
         * @throws Exception 异常
         */
        @JvmStatic
        @Throws(Exception::class)
        fun makeRotate(inputStream: InputStream,
                       outputStream: OutputStream, angle: Int) {
            val dealedImage: BufferedImage
            val res: BufferedImage
            try {
                dealedImage = ImageIO.read(inputStream)
                val width = dealedImage.width
                val height = dealedImage.height

                // calculate the new image size
                val rect_des = CalcRotatedSize(Rectangle(Dimension(
                        width, height)), angle)
                res = BufferedImage(rect_des.width, rect_des.height,
                        BufferedImage.TYPE_INT_RGB)
                val g2 = res.createGraphics()
                // transform
                g2.translate((rect_des.width - width) / 2,
                        (rect_des.height - height) / 2)
                g2.rotate(Math.toRadians(angle.toDouble()), width / 2.0, height / 2.0)

                g2.drawImage(dealedImage, null, null)
                g2.dispose()
                ImageIO.write(res, "png", outputStream)
            } catch (e: IOException) {
                log.error("makeRotate error : {}", e)
                throw Exception(e)
            }

        }

        /**
         * 旋转处理
         */
        @JvmStatic
        fun CalcRotatedSize(src: Rectangle, angel: Int): Rectangle {
            var tempAngel = angel
            // if angel is greater than 90 degree, we need to do some conversion
            tempAngel = Math.abs(tempAngel)
            if (tempAngel >= 90) {
                if (tempAngel / 90 % 2 == 1) {
                    val temp = src.height
                    src.height = src.width
                    src.width = temp
                }
                tempAngel = tempAngel % 90
            }

            val r = Math.sqrt(src.height.toDouble() * src.height + src.width.toDouble() * src.width) / 2
            val len = 2.0 * Math.sin(Math.toRadians(tempAngel.toDouble()) / 2) * r
            val angel_alpha = (Math.PI - Math.toRadians(tempAngel.toDouble())) / 2
            val angel_dalta_width = Math.atan(src.height.toDouble() / src.width)
            val angel_dalta_height = Math.atan(src.width.toDouble() / src.height)

            val len_dalta_width = (len * Math.cos(Math.PI - angel_alpha
                    - angel_dalta_width)).toInt()
            val len_dalta_height = (len * Math.cos(Math.PI - angel_alpha
                    - angel_dalta_height)).toInt()
            val des_width = src.width + len_dalta_width * 2
            val des_height = src.height + len_dalta_height * 2
            return java.awt.Rectangle(Dimension(des_width, des_height))
        }
    }

}