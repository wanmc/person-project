/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2018，所有权利保留。
 * 
 * 项目名：	akkadb-api
 * 文件名：	CycleQueue.java
 * 模块说明：	
 * 修改历史：
 * 2018年1月6日 - wanmc - 创建。
 */
package com.wmc.akkadb.commons;

import java.util.Iterator;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

/**
 * 环状链表
 * 
 * @author wanmc
 */
public class CycleQueue<T> implements Iterable<T> {
  private int size;
  private Node<T> head;

  public CycleQueue<T> add(T t) {
    if (head == null) {
      head = new Node<>(t);
    } else {
      Node<T> tail = head.prev;
      Node<T> node = new Node<>(tail, head, t);
      head.prev = node;
      tail.next = node;
    }
    size++;
    return this;
  }

  public void set(Integer index, T t) {
    Node<T> node = getNode(index);
    node.t = t;
  }

  public CycleQueue<T> remove(int index) {
    Node<T> r = getNode(index);
    return remove(r);
  }

  public CycleQueue<T> remove(Predicate<Node<T>> predicate) {
    Node<T> cusor = head;
    for (int i = 0; i < size; i++) {
      if (predicate.test(cusor)) {
        remove(cusor);
      }
      cusor = cusor.next;
    }
    return this;
  }

  public Node<T> getNode(int index) {
    Node<T> n = head;
    for (int i = index; i > 0; i--) {
      n = n.next;
    }
    return n;
  }

  private CycleQueue<T> remove(Node<T> r) {
    if (size == 1) {
      head = null;
    } else {
      Node<T> prev = r.prev;
      Node<T> next = r.next;
      prev.next = next;
      next.prev = prev;
    }
    return this;
  }
  //
  // public static void main(String[] args) {
  // CycleQueue<Integer> q = new CycleQueue<>();
  // for (int i = 0; i < 11; i++) {
  // q.add(i);
  // }
  // System.out.println(q);
  // q.set(5, 50);
  // System.out.println(q);
  // }

  public T get(int index) {
    return getNode(index).t;
  }

  public int size() {
    return size;
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {
      private Node<T> cusor = new Node<>(null, head, null);

      @Override
      public boolean hasNext() {
        return size > 0 && (cusor.prev == null || cusor.next != head);
      }

      @Override
      public T next() {
        if (hasNext()) {
          cusor = cusor.next;
          return cusor.t;
        }
        return null;
      }
    };
  }

  @Override
  public String toString() {
    return StringUtils.join(this, ", ");
  }

  public static class Node<T> {
    private Node<T> prev;
    private Node<T> next;
    private T t;

    public Node(T t) {
      this.prev = this;
      this.next = this;
      this.t = t;
    }

    public Node(Node prev, Node next, T t) {
      this.prev = prev;
      this.next = next;
      this.t = t;
    }

    public Node<T> prev() {
      return prev;
    }

    public Node<T> next() {
      return next;
    }

    public T get() {
      return t;
    }
  }
}
