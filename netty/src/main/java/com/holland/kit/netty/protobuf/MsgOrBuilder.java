// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: main/resources/protobuf.proto

package com.holland.kit.netty.protobuf;

public interface MsgOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.holland.kit.netty.protobuf.Msg)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   *用于表示当前传递的类型
   * </pre>
   *
   * <code>.com.holland.kit.netty.protobuf.Msg.DataType data_type = 1;</code>
   * @return The enum numeric value on the wire for dataType.
   */
  int getDataTypeValue();
  /**
   * <pre>
   *用于表示当前传递的类型
   * </pre>
   *
   * <code>.com.holland.kit.netty.protobuf.Msg.DataType data_type = 1;</code>
   * @return The dataType.
   */
  com.holland.kit.netty.protobuf.Msg.DataType getDataType();

  /**
   * <code>.com.holland.kit.netty.protobuf.Person person = 2;</code>
   * @return Whether the person field is set.
   */
  boolean hasPerson();
  /**
   * <code>.com.holland.kit.netty.protobuf.Person person = 2;</code>
   * @return The person.
   */
  com.holland.kit.netty.protobuf.Person getPerson();
  /**
   * <code>.com.holland.kit.netty.protobuf.Person person = 2;</code>
   */
  com.holland.kit.netty.protobuf.PersonOrBuilder getPersonOrBuilder();

  /**
   * <code>.com.holland.kit.netty.protobuf.Dog dog = 3;</code>
   * @return Whether the dog field is set.
   */
  boolean hasDog();
  /**
   * <code>.com.holland.kit.netty.protobuf.Dog dog = 3;</code>
   * @return The dog.
   */
  com.holland.kit.netty.protobuf.Dog getDog();
  /**
   * <code>.com.holland.kit.netty.protobuf.Dog dog = 3;</code>
   */
  com.holland.kit.netty.protobuf.DogOrBuilder getDogOrBuilder();

  /**
   * <code>.com.holland.kit.netty.protobuf.Cat cat = 4;</code>
   * @return Whether the cat field is set.
   */
  boolean hasCat();
  /**
   * <code>.com.holland.kit.netty.protobuf.Cat cat = 4;</code>
   * @return The cat.
   */
  com.holland.kit.netty.protobuf.Cat getCat();
  /**
   * <code>.com.holland.kit.netty.protobuf.Cat cat = 4;</code>
   */
  com.holland.kit.netty.protobuf.CatOrBuilder getCatOrBuilder();

  public com.holland.kit.netty.protobuf.Msg.DataBodyCase getDataBodyCase();
}