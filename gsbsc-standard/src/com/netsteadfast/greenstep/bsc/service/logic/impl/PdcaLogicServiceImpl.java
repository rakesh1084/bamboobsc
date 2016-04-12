/* 
 * Copyright 2012-2016 bambooCORE, greenstep of copyright Chen Xin Nien
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * -----------------------------------------------------------------------
 * 
 * author: 	Chen Xin Nien
 * contact: chen.xin.nien@gmail.com
 * 
 */
package com.netsteadfast.greenstep.bsc.service.logic.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.task.Task;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.netsteadfast.greenstep.base.SysMessageUtil;
import com.netsteadfast.greenstep.base.exception.ServiceException;
import com.netsteadfast.greenstep.base.model.DefaultResult;
import com.netsteadfast.greenstep.base.model.GreenStepSysMsgConstants;
import com.netsteadfast.greenstep.base.model.ServiceAuthority;
import com.netsteadfast.greenstep.base.model.ServiceMethodAuthority;
import com.netsteadfast.greenstep.base.model.ServiceMethodType;
import com.netsteadfast.greenstep.base.service.logic.BaseLogicService;
import com.netsteadfast.greenstep.bsc.service.IEmployeeService;
import com.netsteadfast.greenstep.bsc.service.IKpiService;
import com.netsteadfast.greenstep.bsc.service.IOrganizationService;
import com.netsteadfast.greenstep.bsc.service.IPdcaDocService;
import com.netsteadfast.greenstep.bsc.service.IPdcaItemAuditService;
import com.netsteadfast.greenstep.bsc.service.IPdcaItemDocService;
import com.netsteadfast.greenstep.bsc.service.IPdcaItemOwnerService;
import com.netsteadfast.greenstep.bsc.service.IPdcaItemService;
import com.netsteadfast.greenstep.bsc.service.IPdcaKpisService;
import com.netsteadfast.greenstep.bsc.service.IPdcaMeasureFreqService;
import com.netsteadfast.greenstep.bsc.service.IPdcaOrgaService;
import com.netsteadfast.greenstep.bsc.service.IPdcaOwnerService;
import com.netsteadfast.greenstep.bsc.service.IPdcaService;
import com.netsteadfast.greenstep.bsc.service.logic.IPdcaLogicService;
import com.netsteadfast.greenstep.po.hbm.BbEmployee;
import com.netsteadfast.greenstep.po.hbm.BbKpi;
import com.netsteadfast.greenstep.po.hbm.BbOrganization;
import com.netsteadfast.greenstep.po.hbm.BbPdca;
import com.netsteadfast.greenstep.po.hbm.BbPdcaDoc;
import com.netsteadfast.greenstep.po.hbm.BbPdcaItem;
import com.netsteadfast.greenstep.po.hbm.BbPdcaItemAudit;
import com.netsteadfast.greenstep.po.hbm.BbPdcaItemDoc;
import com.netsteadfast.greenstep.po.hbm.BbPdcaItemOwner;
import com.netsteadfast.greenstep.po.hbm.BbPdcaKpis;
import com.netsteadfast.greenstep.po.hbm.BbPdcaMeasureFreq;
import com.netsteadfast.greenstep.po.hbm.BbPdcaOrga;
import com.netsteadfast.greenstep.po.hbm.BbPdcaOwner;
import com.netsteadfast.greenstep.util.BusinessProcessManagementUtils;
import com.netsteadfast.greenstep.vo.EmployeeVO;
import com.netsteadfast.greenstep.vo.KpiVO;
import com.netsteadfast.greenstep.vo.OrganizationVO;
import com.netsteadfast.greenstep.vo.PdcaDocVO;
import com.netsteadfast.greenstep.vo.PdcaItemAuditVO;
import com.netsteadfast.greenstep.vo.PdcaItemDocVO;
import com.netsteadfast.greenstep.vo.PdcaItemOwnerVO;
import com.netsteadfast.greenstep.vo.PdcaItemVO;
import com.netsteadfast.greenstep.vo.PdcaKpisVO;
import com.netsteadfast.greenstep.vo.PdcaMeasureFreqVO;
import com.netsteadfast.greenstep.vo.PdcaOrgaVO;
import com.netsteadfast.greenstep.vo.PdcaOwnerVO;
import com.netsteadfast.greenstep.vo.PdcaVO;
import com.netsteadfast.greenstep.vo.SysBpmnResourceVO;

@ServiceAuthority(check=true)
@Service("bsc.service.logic.PdcaLogicService")
@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
public class PdcaLogicServiceImpl extends BaseLogicService implements IPdcaLogicService {
	protected Logger logger=Logger.getLogger(PdcaLogicServiceImpl.class);
	private static final int MAX_DESCRIPTION_LENGTH = 500;
	private IPdcaService<PdcaVO, BbPdca, String> pdcaService;
	private IPdcaDocService<PdcaDocVO, BbPdcaDoc, String> pdcaDocService;
	private IPdcaKpisService<PdcaKpisVO, BbPdcaKpis, String> pdcaKpisService;
	private IPdcaOrgaService<PdcaOrgaVO, BbPdcaOrga, String> pdcaOrgaService;
	private IPdcaOwnerService<PdcaOwnerVO, BbPdcaOwner, String> pdcaOwnerService;
	private IPdcaMeasureFreqService<PdcaMeasureFreqVO, BbPdcaMeasureFreq, String> pdcaMeasureFreqService;
	private IPdcaItemService<PdcaItemVO, BbPdcaItem, String> pdcaItemService;
	private IPdcaItemAuditService<PdcaItemAuditVO, BbPdcaItemAudit, String> pdcaItemAuditService;
	private IPdcaItemDocService<PdcaItemDocVO, BbPdcaItemDoc, String> pdcaItemDocService;
	private IPdcaItemOwnerService<PdcaItemOwnerVO, BbPdcaItemOwner, String> pdcaItemOwnerService;
	private IKpiService<KpiVO, BbKpi, String> kpiService;
	private IEmployeeService<EmployeeVO, BbEmployee, String> employeeService;
	private IOrganizationService<OrganizationVO, BbOrganization, String> organizationService;
	
	public PdcaLogicServiceImpl() {
		super();
	}
	
	public IPdcaService<PdcaVO, BbPdca, String> getPdcaService() {
		return pdcaService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaService")
	@Required		
	public void setPdcaService(IPdcaService<PdcaVO, BbPdca, String> pdcaService) {
		this.pdcaService = pdcaService;
	}

	@Override
	public String getBusinessProcessManagementResourceId() {
		return "PDCAProjectProcess";
	}

	public IPdcaDocService<PdcaDocVO, BbPdcaDoc, String> getPdcaDocService() {
		return pdcaDocService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaDocService")
	@Required
	public void setPdcaDocService(IPdcaDocService<PdcaDocVO, BbPdcaDoc, String> pdcaDocService) {
		this.pdcaDocService = pdcaDocService;
	}

	public IPdcaKpisService<PdcaKpisVO, BbPdcaKpis, String> getPdcaKpisService() {
		return pdcaKpisService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaKpisService")
	@Required
	public void setPdcaKpisService(IPdcaKpisService<PdcaKpisVO, BbPdcaKpis, String> pdcaKpisService) {
		this.pdcaKpisService = pdcaKpisService;
	}
	
	public IPdcaOrgaService<PdcaOrgaVO, BbPdcaOrga, String> getPdcaOrgaService() {
		return pdcaOrgaService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaOrgaService")
	@Required
	public void setPdcaOrgaService(IPdcaOrgaService<PdcaOrgaVO, BbPdcaOrga, String> pdcaOrgaService) {
		this.pdcaOrgaService = pdcaOrgaService;
	}

	public IPdcaOwnerService<PdcaOwnerVO, BbPdcaOwner, String> getPdcaOwnerService() {
		return pdcaOwnerService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaOwnerService")
	@Required	
	public void setPdcaOwnerService(IPdcaOwnerService<PdcaOwnerVO, BbPdcaOwner, String> pdcaOwnerService) {
		this.pdcaOwnerService = pdcaOwnerService;
	}
	
	public IPdcaMeasureFreqService<PdcaMeasureFreqVO, BbPdcaMeasureFreq, String> getPdcaMeasureFreqService() {
		return pdcaMeasureFreqService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaMeasureFreqService")
	@Required
	public void setPdcaMeasureFreqService(
			IPdcaMeasureFreqService<PdcaMeasureFreqVO, BbPdcaMeasureFreq, String> pdcaMeasureFreqService) {
		this.pdcaMeasureFreqService = pdcaMeasureFreqService;
	}
	
	public IPdcaItemService<PdcaItemVO, BbPdcaItem, String> getPdcaItemService() {
		return pdcaItemService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaItemService")
	@Required	
	public void setPdcaItemService(IPdcaItemService<PdcaItemVO, BbPdcaItem, String> pdcaItemService) {
		this.pdcaItemService = pdcaItemService;
	}

	public IPdcaItemAuditService<PdcaItemAuditVO, BbPdcaItemAudit, String> getPdcaItemAuditService() {
		return pdcaItemAuditService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaItemAuditService")
	@Required
	public void setPdcaItemAuditService(
			IPdcaItemAuditService<PdcaItemAuditVO, BbPdcaItemAudit, String> pdcaItemAuditService) {
		this.pdcaItemAuditService = pdcaItemAuditService;
	}
	
	public IPdcaItemDocService<PdcaItemDocVO, BbPdcaItemDoc, String> getPdcaItemDocService() {
		return pdcaItemDocService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaItemDocService")
	@Required	
	public void setPdcaItemDocService(IPdcaItemDocService<PdcaItemDocVO, BbPdcaItemDoc, String> pdcaItemDocService) {
		this.pdcaItemDocService = pdcaItemDocService;
	}

	public IPdcaItemOwnerService<PdcaItemOwnerVO, BbPdcaItemOwner, String> getPdcaItemOwnerService() {
		return pdcaItemOwnerService;
	}

	@Autowired
	@Resource(name="bsc.service.PdcaItemOwnerService")
	@Required		
	public void setPdcaItemOwnerService(
			IPdcaItemOwnerService<PdcaItemOwnerVO, BbPdcaItemOwner, String> pdcaItemOwnerService) {
		this.pdcaItemOwnerService = pdcaItemOwnerService;
	}

	public IKpiService<KpiVO, BbKpi, String> getKpiService() {
		return kpiService;
	}

	@Autowired
	@Resource(name="bsc.service.KpiService")
	@Required
	public void setKpiService(IKpiService<KpiVO, BbKpi, String> kpiService) {
		this.kpiService = kpiService;
	}

	public IEmployeeService<EmployeeVO, BbEmployee, String> getEmployeeService() {
		return employeeService;
	}

	@Autowired
	@Resource(name="bsc.service.EmployeeService")
	@Required
	public void setEmployeeService(IEmployeeService<EmployeeVO, BbEmployee, String> employeeService) {
		this.employeeService = employeeService;
	}

	public IOrganizationService<OrganizationVO, BbOrganization, String> getOrganizationService() {
		return organizationService;
	}

	@Autowired
	@Resource(name="bsc.service.OrganizationService")
	@Required
	public void setOrganizationService(IOrganizationService<OrganizationVO, BbOrganization, String> organizationService) {
		this.organizationService = organizationService;
	}

	@Override
	public SysBpmnResourceVO getBusinessProcessManagementResourceObject(String resourceId) throws ServiceException, Exception {
		return BusinessProcessManagementUtils.loadResource(getBusinessProcessManagementResourceId());
	}

	@Override
	public String startProcess(Map<String, Object> paramMap) throws Exception {
		return BusinessProcessManagementUtils.startProcess(this.getBusinessProcessManagementResourceId(), paramMap);
	}

	@Override
	public void completeTask(String taskId, Map<String, Object> paramMap) throws Exception {
		BusinessProcessManagementUtils.completeTask(taskId, paramMap);
	}

	@Override
	public List<Task> queryTask() throws Exception {
		return BusinessProcessManagementUtils.queryTask( this.getBusinessProcessManagementResourceId() );
	}	
	
	@ServiceMethodAuthority(type={ServiceMethodType.INSERT})
	@Transactional(
			propagation=Propagation.REQUIRED, 
			readOnly=false,
			rollbackFor={RuntimeException.class, IOException.class, Exception.class} )		
	@Override
	public DefaultResult<PdcaVO> create(PdcaVO pdca, List<String> organizationOids, List<String> employeeOids,
			List<String> attachment, List<PdcaItemVO> items) throws ServiceException, Exception {
		if (null == pdca || null == items || items.size()<1 || organizationOids.size()<1 || employeeOids.size()<1) {
			throw new ServiceException(SysMessageUtil.get(GreenStepSysMsgConstants.PARAMS_BLANK));
		}
		this.setStringValueMaxLength(pdca, "description", MAX_DESCRIPTION_LENGTH);
		DefaultResult<PdcaVO> result = this.pdcaService.saveObject(pdca);
		if (result.getValue() == null) {
			throw new ServiceException(result.getSystemMessage().getValue());
		}
		
		return result;
	}

	@ServiceMethodAuthority(type={ServiceMethodType.UPDATE})
	@Transactional(
			propagation=Propagation.REQUIRED, 
			readOnly=false,
			rollbackFor={RuntimeException.class, IOException.class, Exception.class} )		
	@Override
	public DefaultResult<PdcaVO> update(PdcaVO pdca, List<String> organizationOids, List<String> employeeOids,
			List<String> attachment, List<PdcaItemVO> items) throws ServiceException, Exception {
		
		return null;
	}

	@ServiceMethodAuthority(type={ServiceMethodType.DELETE})
	@Transactional(
			propagation=Propagation.REQUIRED, 
			readOnly=false,
			rollbackFor={RuntimeException.class, IOException.class, Exception.class} )				
	@Override
	public DefaultResult<Boolean> delete(PdcaVO pdca) throws ServiceException, Exception {
		
		return null;
	}

}
