package com.fileServer.core.hibernate.dao;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

import com.fileServer.core.hibernate.pojo.FileDetail;
import com.fileServer.core.hibernate.pojo.FileSubLocation;
import com.fileServer.core.util.MathOperator;

@Repository("uploadDao")
public class UploadDaoImpl implements UploadDao{

    private static final Logger logger = LoggerFactory.getLogger(UploadDaoImpl.class);

    /*
    private BizHibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new BizHibernateTemplate(sessionFactory);
    }
*/
    
	@PersistenceContext
	private EntityManager entityManager;
    
	@Override
	public List<String> findAllAvailableSubfolders() {
    	StringBuilder q = new StringBuilder("FROM FileSubLocation where size<maxsize");
//    	List<FileSubLocation> subLocs = hibernateTemplate.find(q.toString());
    	Query query = entityManager.createQuery(q.toString());
    	List<FileSubLocation> subLocs = query.getResultList();
    	List<String> locs = null;
    	if(subLocs!=null && subLocs.size()>0){
    		locs = new ArrayList<String>();
    		for(FileSubLocation l : subLocs){
    			locs.add(l.getSubfolder());
    		}
    	}
    	return locs;
	}

    @Override
	public String findAvailableSubfolder() {
    	StringBuilder q = new StringBuilder("FROM FileSubLocation where size<maxsize");
//    	List<FileSubLocation> locs = hibernateTemplate.find(q.toString());
    	Query query = entityManager.createQuery(q.toString());
    	List<FileSubLocation> locs = query.getResultList();
    	if(locs!=null && locs.size()>0){
    		return locs.get(0).getSubfolder();
    	}
    	
		return null;
	}

	@Override
	public Long saveFileDetail(FileDetail fileDetail) {
		if(fileDetail.getId()!=null){
//			hibernateTemplate.update(fileDetail);
//			return fileDetail.getId();
			return entityManager.merge(fileDetail).getId();
		}else{
//			return (Long)hibernateTemplate.save(fileDetail);
			entityManager.persist(fileDetail);
			return fileDetail.getId();
		}
	}

	@Override
	public void updateSizeForSublocation(MathOperator op, int number, String subfolder) {
		if(StringUtils.isNotBlank(subfolder) && op!=null){
			// find FileSubLocation by subfolder
			StringBuilder q = new StringBuilder("FROM FileSubLocation where subfolder = '").append(subfolder).append("'");
//			List<FileSubLocation> locs = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<FileSubLocation> locs = query.getResultList();
			if(locs!=null && locs.size()>0){
				if(locs.size()>1) logger.error(new StringBuilder("More than one \"").append(subfolder).append("\" in the system!").toString());
				
				FileSubLocation subLocation = locs.get(0);
				if(op.equals(MathOperator.ADD)){
					subLocation.setSize(subLocation.getSize()+number);
				}else if(op.equals(MathOperator.SUBTRACT)){
					subLocation.setSize((subLocation.getSize()-number>=0)?subLocation.getSize()-number:0);
				}
//				hibernateTemplate.update(subLocation);
				entityManager.merge(subLocation);
			}
			
		}
	}

	@Override
	public Long saveFileSubLocation(FileSubLocation loc) {
		if(loc!=null){
			if(loc.getId()!=null){
//				hibernateTemplate.update(loc);
//				return loc.getId();
				return entityManager.merge(loc).getId();
			}else{
				// check duplicate:
				FileSubLocation existLoc = findFileSubLocationByFoldername(loc.getSubfolder());
				if(existLoc!=null){
					return existLoc.getId();
				}else{
//					return (Long)hibernateTemplate.save(loc);
					entityManager.persist(loc);
					return loc.getId();
				}
			}
		}
		return null;
	}

	@Override
	public FileSubLocation findFileSubLocationByFoldername(String foldername) {
		if(StringUtils.isNotBlank(foldername)){
			StringBuilder q = new StringBuilder("FROM FileSubLocation where subfolder = '").append(foldername).append("'");
//			List<FileSubLocation> locs = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<FileSubLocation> locs = query.getResultList();
			if(locs!=null && locs.size()>0){
				return locs.get(0);
			}
		}
		return null;
	}

	@Override
	public FileDetail getFileDetailBySystemName(String fileSystemName) {
		if(StringUtils.isNotBlank(fileSystemName)){
			StringBuilder q = new StringBuilder("FROM FileDetail where systemname = '").append(fileSystemName).append("'");
//			List<FileDetail> fdetails = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<FileDetail> fdetails = query.getResultList();
			if(fdetails!=null && fdetails.size()>0) return fdetails.get(0);
			
		}
		return null;
	}

}
