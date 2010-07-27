package randoop.plugin.internal.ui.launching;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;


public class RandoopLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

  @Override
    public void createTabs(ILaunchConfigurationDialog arg0, String arg1) {
    ILaunchConfigurationTab[] tabs= new ILaunchConfigurationTab[] {
        new GeneralTab(),
        new ParametersTab(),
        new JavaClasspathTab(),
        new JavaJRETab(),
        new EnvironmentTab(),
        new CommonTab()
    };
    setTabs(tabs);
  }
}
