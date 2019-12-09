package cn.javaer.snippetsbox.model;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 树结构.
 *
 * @author cn-src
 */
public class Tree {

    private Tree() {
    }

    /**
     * 将实体列表数据转换成树结构，比如多级区域数据转换成前端 UI 组件需要的 Tree 结构.
     *
     * @param models 二维表结构的实体数据
     * @param fns 实体的哪些字段 getter 用于转换成树
     * @param <E> 实体类型
     * @param <T> Tree 节点类型
     *
     * @return 根节点的所有子节点
     */
    @SafeVarargs
    public static <E, T> List<TreeNode<T>> of(List<E> models, Function<E, T>... fns) {
        Objects.requireNonNull(fns);

        if (models == null || models.isEmpty()) {
            return Collections.emptyList();
        }

        TreeNode<T> root = new TreeNode<>();
        TreeNode<T> current = root;

        for (E row : models) {
            for (Function<E, T> fn : fns) {
                T cell = fn.apply(row);

                if (current.getChildren() == null) {
                    current.setChildren(new ArrayList<>());
                }

                Optional<TreeNode<T>> first = current.getChildren().stream()
                        .filter(it -> Objects.equals(cell, it.getTitle()))
                        .findFirst();
                if (first.isPresent()) {
                    current = first.get();
                }
                else {
                    TreeNode<T> treeNode = new TreeNode<>(cell);
                    current.getChildren().add(treeNode);
                    current = treeNode;
                }
            }
            current = root;
        }
        return root.getChildren();
    }

    /**
     * 将 Tree 节点数据转换成二维表结构.
     *
     * @param treeNodes Tree 节点数据
     * @param createFn 实体类创建函数
     * @param fns 实体类 setter 函数
     * @param <E> 实体类型
     * @param <T> Tree 节点类型
     *
     * @return 实体列表
     */
    @SafeVarargs
    public static <E, T> List<E> toModel(List<TreeNode<T>> treeNodes, Supplier<E> createFn, BiConsumer<E, T>... fns) {
        Objects.requireNonNull(createFn);
        Objects.requireNonNull(fns);

        if (treeNodes == null || treeNodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<E> result = new ArrayList<>();
        TreeNode<T> current = new TreeNode<>();
        current.setChildren(new ArrayList<>(treeNodes));
        LinkedList<TreeNode<T>> stack = new LinkedList<>();
        stack.push(current);

        // 遍历树结构，转换成二维表结构用于存库
        // 从根节点，遍历到叶子节点，为数据库一条记录，同时移除此叶子节点
        // 当前迭代的节点往根节点方向，以及同级的下级节点移动
        while (null != current) {
            if (!CollectionUtils.isEmpty(current.getChildren())) {
                current = current.getChildren().get(0);
                stack.push(current);
            }
            else {
                int size = Math.min(stack.size(), fns.length);
                E e = createFn.get();
                for (int i = 1; i < size; i++) {
                    fns[i].accept(e, stack.get(i).getTitle());
                }
                result.add(e);
                stack.pop();

                TreeNode<T> peek = stack.peek();
                if (peek != null) {
                    peek.getChildren().remove(0);
                }

                // 迭代清理一条线的所有孤叶节点
                while (peek != null && (peek.getChildren() == null || peek.getChildren().isEmpty())) {
                    stack.pop();
                    if (stack.isEmpty()) {
                        peek = null;
                        break;
                    }
                    peek = stack.peek();
                    if (peek.getChildren() != null && !peek.getChildren().isEmpty()) {
                        peek.getChildren().remove(0);
                    }
                }
                current = peek;
            }
        }
        return result;
    }
}