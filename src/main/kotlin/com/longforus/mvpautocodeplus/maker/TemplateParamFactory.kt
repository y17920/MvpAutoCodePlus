package com.longforus.mvpautocodeplus.maker

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.longforus.mvpautocodeplus.*
import com.longforus.mvpautocodeplus.config.ItemConfigBean
import java.util.*

/**
 * Created by XQ Yang on 2018/6/28  14:18.
 * Description : template参数工厂
 */

object TemplateParamFactory {

    private var state: PropertiesComponent? = null

    fun getParam4TemplateName(it: ItemConfigBean, templateName: String, enterName: String, superImplName: String, contract: PsiFile?,
                              mSelectedState: PropertiesComponent): Map<String, String?> {
        this.state = mSelectedState
        val liveTemplateParam = HashMap<String, String?>()
        when (templateName) {
/*            CONTRACT_TP_NAME_JAVA, CONTRACT_TP_NAME_KOTLIN, CONTRACT_TP_NO_MODEL_NAME_JAVA, CONTRACT_TP_NO_MODEL_NAME_KOTLIN -> {
                //com.longforus.mvpautocodeplus.maker  方形
//                val (superVNameNoGeneric, superVGenericValue) = getNameAndGenericType(SUPER_VIEW)
//                val (superPNameNoGeneric, superPGenericValue) = getNameAndGenericType(SUPER_PRESENTER)
                val (superMNameNoGeneric, superMGenericValue) = getNameAndGenericType(SUPER_MODEL)
                liveTemplateParam["V"] = superVNameNoGeneric
                liveTemplateParam["M"] = superMNameNoGeneric
                liveTemplateParam["P"] = superPNameNoGeneric
                liveTemplateParam["VN"] = superVNameNoGeneric?.substring(superVNameNoGeneric.lastIndexOf('.')+1 until superVNameNoGeneric.length)?: "IView"
                liveTemplateParam["MN"] = superMNameNoGeneric?.substring(superMNameNoGeneric.lastIndexOf('.')+1 until superMNameNoGeneric.length)?: "IModel"
                liveTemplateParam["PN"] = superPNameNoGeneric?.substring(superPNameNoGeneric.lastIndexOf('.')+1 until superPNameNoGeneric.length)?: "IPresenter"

                liveTemplateParam["P_OR_M"] = getPresenterOrViewModel(liveTemplateParam["PN"])
                if (liveTemplateParam["P_OR_M"]=="ViewModel"&&superVGenericValue.contains("Presenter")) {
                    liveTemplateParam["VG"] = superVGenericValue.replace("Presenter","ViewModel")
                }else{
                    liveTemplateParam["VG"] = superVGenericValue
                }
                liveTemplateParam["PG"] = superPGenericValue
                liveTemplateParam["MG"] = superMGenericValue
            }*/
            VIEW_IMPL_TP_ACTIVITY_JAVA, VIEW_IMPL_TP_ACTIVITY_KOTLIN -> {
                setCommonParam(enterName, superImplName, contract, liveTemplateParam, templateName,it)
                if (templateName == VIEW_IMPL_TP_ACTIVITY_JAVA) {
                    liveTemplateParam["IMPL_TYPE"] = "Activity"
                }
                liveTemplateParam["TYPE"] = "View"
                val nameSb  = StringBuilder()
                it.name.forEach {
                    if (it >= "A"[0] && it <= "Z"[0]) {
                        nameSb.append("_")
                    }
                    nameSb.append(it)
                }
                liveTemplateParam["LAYOUT"] = getAcLayoutFileName(nameSb.toString())
            }
            VIEW_IMPL_TP_FRAGMENT_JAVA, VIEW_IMPL_TP_FRAGMENT_KOTLIN -> {
                setCommonParam(enterName, superImplName, contract, liveTemplateParam, templateName,it)
                if (templateName == VIEW_IMPL_TP_FRAGMENT_JAVA) {
                    liveTemplateParam["IMPL_TYPE"] = "Fragment"
                }
                liveTemplateParam["TYPE"] = "View"

                val nameSb  = StringBuilder()
                it.name.forEach {
                    if (it >= "A"[0] && it <= "Z"[0]) {
                        nameSb.append("_")
                    }
                    nameSb.append(it)
                }
                liveTemplateParam["LAYOUT"] = getFaLayoutFileName(nameSb.toString())
            }
            PRESENTER_IMPL_TP_JAVA, PRESENTER_IMPL_TP_KOTLIN -> {
                setCommonParam(enterName, superImplName, contract, liveTemplateParam, templateName,it)
                if (templateName == PRESENTER_IMPL_TP_JAVA) {
                    liveTemplateParam["IMPL_TYPE"] = "ViewModel"
                }
                liveTemplateParam["TYPE"] = "ViewModel"
                val nameSb  = StringBuilder()
                it.name.forEach {
                    if (it >= "A"[0] && it <= "Z"[0]) {
                        nameSb.append("_")
                    }
                    nameSb.append(it)
                }
                liveTemplateParam["LOYOUT"] = getFaLayoutFileName(nameSb.toString())
            }
            MODEL_IMPL_TP_JAVA, MODEL_IMPL_TP_KOTLIN -> {
                setCommonParam(enterName, superImplName, contract, liveTemplateParam, templateName,it)
                if (templateName == MODEL_IMPL_TP_JAVA) {
                    liveTemplateParam["IMPL_TYPE"] = "Model"
                }
                liveTemplateParam["TYPE"] = "Model"
            }
//                <I${NAME}Contract.View,I${NAME}Contract.Presenter>
        }
        return liveTemplateParam
    }

    fun getPresenterOrViewModel(superName: String?): String? {
        if(superName.isNullOrEmpty()){
            return null
        }
        return if(superName.endsWith("ViewModel")) "ViewModel" else "Presenter"
    }


    private fun setCommonParam(name: String, superImplName: String, contract: PsiFile?,
        liveTemplateParam: HashMap<String, String?>,
        templateName: String,it: ItemConfigBean) {
        val (noGSuperName, superMGenericValue) = getNameAndGenericType("", false, name, superImplName,it,templateName)
//        var packageName = ""
//        if (contract is PsiJavaFile) {
//            packageName = contract.packageName
//        } else if (contract is org.jetbrains.kotlin.psi.KtFile) {
//            packageName = contract.packageFqName.asString()
//        }

//        liveTemplateParam["CONTRACT"] = "$packageName.${getContractName(name)}"
        liveTemplateParam["IMPL"] = noGSuperName
        liveTemplateParam["VG"] = superMGenericValue

        if (templateName.startsWith("Kotlin")) {
            if (noGSuperName.isNullOrEmpty()) {
                liveTemplateParam["IMPL_NP"] = ""
            } else {
                liveTemplateParam["IMPL_NP"] = liveTemplateParam["IMPL"]?.lastDotContent()
            }
            liveTemplateParam["CONTRACT_NP"] = liveTemplateParam["CONTRACT"]?.lastDotContent()

        }
    }


    fun getNameAndGenericType(type: String, isContract: Boolean = true, enterName: String = "",
                              selectedValue: String = "",itBean: ItemConfigBean, templateName: String): Pair<String?, String> {
        if (selectedValue.startsWith(IS_NOT_SET)) {
            return "" to ""
        }
        var presenterOrViewModel = getPresenterOrViewModel(state?.getValue(SUPER_PRESENTER_IMPL))

        val setValue = if (selectedValue.isNotEmpty()) selectedValue else state?.getValue(type)
        if (setValue.isNullOrEmpty()) {
            Messages.showErrorDialog("Super Interface name is null !", "Error")
            throw IllegalArgumentException("Super Interface name is null !")
        }
        var dataBing = ""
        when (templateName){
            VIEW_IMPL_TP_ACTIVITY_JAVA, VIEW_IMPL_TP_ACTIVITY_KOTLIN -> {
                dataBing = "Activity${itBean.name}${getBingFileName()}"
            }
            VIEW_IMPL_TP_FRAGMENT_JAVA, VIEW_IMPL_TP_FRAGMENT_KOTLIN -> {
                dataBing = "Fragment${itBean.name}${getBingFileName()}"
            }
        }
        val indexOf = setValue.indexOf("<")
        var generic = ""
        var resultName = setValue
        if (indexOf > -1) {
            resultName = setValue.substring(0, indexOf)
            val g = setValue.substring(indexOf, setValue.length)
            val sb = StringBuilder()
            g.forEach {
                when (it) {
                    "B"[0] -> sb.append(if (isContract) "View" else dataBing)
                    "N"[0] -> sb.append(if (isContract) "View" else (itBean.networkData))
                    "R"[0] -> sb.append(if (isContract) "View" else (itBean.resultData))
                    "V"[0] -> sb.append(if (isContract) "View" else "${(enterName)}View")
                    "P"[0] -> sb.append(if (isContract) "Presenter" else "${(enterName)}ViewModel")
                    "M"[0] -> sb.append(if (isContract) "Model" else "${(enterName)}Model")
/*                    "V"[0] -> sb.append(if (isContract) "View" else "${getContractName(enterName)}.View")
                    "P"[0] -> sb.append(if (isContract) "Presenter" else "${getContractName(enterName)}.Presenter")
                    "M"[0] -> sb.append(if (isContract) "Model" else "${getContractName(enterName)}.Model")*/
                    else -> sb.append(it)
                }
            }
            generic = sb.toString()
        }
        return resultName to generic
    }




}

