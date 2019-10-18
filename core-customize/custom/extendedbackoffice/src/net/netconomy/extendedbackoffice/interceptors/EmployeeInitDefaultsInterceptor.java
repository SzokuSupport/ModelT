package net.netconomy.extendedbackoffice.interceptors;

import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.interceptor.InitDefaultsInterceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author tim.ollmann@netconomy.net
 */
public class EmployeeInitDefaultsInterceptor implements InitDefaultsInterceptor<EmployeeModel> {

    @Override
    public void onInitDefaults(EmployeeModel employeeModel, InterceptorContext interceptorContext) {
        employeeModel.setLastPasswordChange(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
}
