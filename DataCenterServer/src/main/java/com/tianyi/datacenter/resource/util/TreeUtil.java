package com.tianyi.datacenter.resource.util;

import com.tianyi.datacenter.resource.entity.DataObjectType;

import java.util.ArrayList;
import java.util.List;

public class TreeUtil {
    /**
     * 使用递归方法建树
     *
     * @param treeNodes
     * @return
     */
    public static List<DataObjectType> buildByRecursive(List<DataObjectType> treeNodes, int root) {
        List<DataObjectType> trees = new ArrayList<DataObjectType>();

        for (DataObjectType treeNode : treeNodes) {
            if (root == treeNode.getFatherId()) {
                trees.add(findChildren(treeNode, treeNodes));
            }
        }
        return trees;
    }

    /**
     * 递归查找子节点
     *
     * @param treeNodes
     * @return
     */
    public static DataObjectType findChildren(DataObjectType treeNode, List<DataObjectType> treeNodes) {
        for (DataObjectType it : treeNodes) {
            if (treeNode.getId() == it.getFatherId()) {
                if (treeNode.getChildren() == null) {
                    treeNode.setChildren(new ArrayList<DataObjectType>());
                }
                treeNode.add(findChildren(it, treeNodes));
            }
        }
        return treeNode;
    }
}
