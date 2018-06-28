package com.longforus.mvpautocodeplus;

/**
 * Created by XQ Yang on 2018/6/28  11:02.
 * Description :
 */

public interface TemplateCons {
    String CONTRACT_TP_CONTENT_JAVA =
        "#if (${PACKAGE_NAME} != \"\")package ${PACKAGE_NAME};#end\n" + "\n" + "import ${V};\n" + "import ${P};\n" + "import ${M};\n" + "/**\n" + " * Description : \n" +
            " * @author  ${USER}\n" + " * @date ${DATE}  ${TIME}\n" + " * \t\t\t\t\t\t\t\t - generic by MvpAutoCodePlus plugin.\n" + " */\n" + "\n" +
            "public interface I${NAME}Contract {\n" + "interface View extends IView${VG}{}\n" + "interface Presenter extends IPresenter${PG}{}\n" +
            "interface Model extends IModel${MG}{}\n" + "}\n";
}