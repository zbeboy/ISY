package top.zbeboy.isy.web.jcaptcha

import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator
import com.octo.captcha.component.image.color.RandomListColorGenerator
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator
import com.octo.captcha.component.image.textpaster.GlyphsPaster
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage
import com.octo.captcha.component.word.FileDictionary
import com.octo.captcha.component.word.wordgenerator.ComposeDictionaryWordGenerator
import com.octo.captcha.engine.image.ListImageCaptchaEngine
import com.octo.captcha.image.gimpy.GimpyFactory
import java.awt.Color
import java.awt.Font

/**
 * Created by zbeboy 2017-11-20 .
 **/
class GMailEngine : ListImageCaptchaEngine() {
    override fun buildInitialFactories() {

        val minWordLength = 4
        val maxWordLength = 5
        val fontSize = 32
        val imageWidth = 100
        val imageHeight = 36

        //word generator
        val dictionnaryWords = ComposeDictionaryWordGenerator(FileDictionary("toddlist"))

        //word2image components
        val randomPaster = GlyphsPaster(
                minWordLength,
                maxWordLength,
                RandomListColorGenerator(arrayOf(Color(23, 170, 27), Color(220, 34, 11), Color(23, 67, 172))),
                arrayOf(TranslateGlyphsVerticalRandomVisitor(1.0), OverlapGlyphsUsingShapeVisitor(3.0), TranslateAllToRandomPointVisitor())
        )

        val background = UniColorBackgroundGenerator(imageWidth, imageHeight, Color.white)
        val font = RandomFontGenerator(
                fontSize,
                fontSize,
                arrayOf(Font("nyala", Font.BOLD, fontSize), Font("Bell MT", Font.PLAIN, fontSize), Font("Credit valley", Font.BOLD, fontSize)))
        val postDef = ImageDeformationByFilters(arrayOf())
        val backDef = ImageDeformationByFilters(arrayOf())
        val textDef = ImageDeformationByFilters(arrayOf())
        val word2image = DeformedComposedWordToImage(font, background, randomPaster, backDef, textDef, postDef)
        addFactory(GimpyFactory(dictionnaryWords, word2image))
    }
}