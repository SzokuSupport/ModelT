package net.netconomy.extendedbackoffice.interceptors;

import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author tim.ollmann@netconomy.net
 */
public class EmployeePrepareInterceptor implements PrepareInterceptor<EmployeeModel> {

    @Override
    public void onPrepare(EmployeeModel employeeModel, InterceptorContext interceptorContext) throws InterceptorException {
        if (interceptorContext.isModified(employeeModel, EmployeeModel.PASSWORD)
            || interceptorContext.isModified(employeeModel, EmployeeModel.ENCODEDPASSWORD)) {
            employeeModel.setLastPasswordChange(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
    }
}
