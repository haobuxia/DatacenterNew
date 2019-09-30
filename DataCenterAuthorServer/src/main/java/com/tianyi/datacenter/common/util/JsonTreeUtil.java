package com.tianyi.datacenter.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonTreeUtil {
    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return·
     */
    public static List<Map<String, Object>> buildByRecursive(List<Map<String, Object>> treeNodes, String root, String idKey, String pidKey) {
        List<Map<String, Object>> trees = new ArrayList();

        for (Map<String, Object> treeNode : treeNodes) {
            if (root.equals(treeNode.get(pidKey).toString())) {
                trees.add(findChildren(treeNode, treeNodes, idKey, pidKey));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @param idKey
     *@param pidKey
     * @return
     */
    public static Map<String, Object> findChildren(Map<String, Object> treeNode, List<Map<String, Object>> treeNodes, String idKey, String pidKey) {
        for (Map<String, Object> it : treeNodes) {
            if (treeNode.get(idKey).toString().equals(it.get(pidKey).toString())) {
                if (treeNode.get("children") == null) {
                    treeNode.put("children",new ArrayList<Map<String, Object>>());
                }
                ((ArrayList<Map<String, Object>>)treeNode.get("children")).add(findChildren(it, treeNodes, idKey, pidKey));
            }
        }
        return treeNode;
    }

    public static String findRootId(List<Map<String, Object>> treeNodes, String idKey, String pidKey) {
        String root="999";
        if(treeNodes.size()>0){
            if(treeNodes.size()==1) {
                return treeNodes.get(0).get(idKey).toString();
            }
            return findFatherId(treeNodes.subList(1,treeNodes.size()),  treeNodes.get(0).get(pidKey).toString(), idKey, pidKey);
        }
        return root;
    }
    private static String findFatherId(List<Map<String, Object>> treeNodes, String fatherId, String idKey, String pidKey) {
        for (Map<String, Object> treeNode : treeNodes) {
            if(fatherId.equals(treeNode.get(idKey).toString())) {
                return findFatherId(treeNodes, treeNode.get(pidKey).toString(), idKey, pidKey);
            }
        }
        return fatherId;
    }
    public static Map<String, Object> findRoot(List<Map<String, Object>> treeNodes, String idKey, String pidKey) {
        if(treeNodes.size()>0){
            if(treeNodes.size()==1) {
                return treeNodes.get(0);
            }
            return findFather(treeNodes.subList(1,treeNodes.size()),  treeNodes.get(0), idKey, pidKey);
        }
        return null;
    }
    private static Map<String, Object> findFather(List<Map<String, Object>> treeNodes, Map<String, Object> current, String idKey, String pidKey) {
        for (Map<String, Object> treeNode : treeNodes) {
            if(current.get(pidKey).toString().equals(treeNode.get(idKey).toString())) {
                return findFather(treeNodes, treeNode, idKey, pidKey);
            }
        }
        return current;
    }

    public static String findFullPath(Map<String, Object> map, List<Map<String, Object>> deptList, String idKey, String pidKey) {
        String fullPath = map.get(idKey).toString();
        return findFullPath(fullPath,map,deptList,idKey,pidKey);
    }
    public static String findFullPath(String fullPath, Map<String, Object> map, List<Map<String, Object>> deptList, String idKey, String pidKey) {
        for (Map<String, Object> map1 : deptList) {
            if(map.get(pidKey).toString().equals(map1.get(idKey).toString())) {
                fullPath = map1.get(idKey).toString() + "," + fullPath;
                return findFullPath(fullPath, map1, deptList, idKey, pidKey);
            }
        }
        return fullPath;
    }
}
