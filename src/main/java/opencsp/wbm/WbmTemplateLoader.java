package opencsp.wbm;

import opencsp.Log;
import spark.template.jade.loader.SparkClasspathTemplateLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


public class WbmTemplateLoader extends SparkClasspathTemplateLoader {
    private static final String TAG = "WbmTemplateLoader";

    private String templateRoot;

    public WbmTemplateLoader(String templateRoot) {
        super(templateRoot);
        Log.d(TAG, "templateRoot=" + templateRoot);
        this.templateRoot = templateRoot;
    }

    @Override
    public Reader getReader(String name) throws IOException {
        File template = new File(templateRoot, name);
        if(template.canRead()) {
            return new FileReader(template);
        } else {
            try {
                return super.getReader(name);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                ex.printStackTrace();
            }
        }
        return null;
    }
}
