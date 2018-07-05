package com.licola.route.compiler;

/**
 * Created by LiCola on 2018/7/2.
 */
public class Constants {


  static final String KEY_MODULE_NAME = "moduleName";

  static final String PACKAGE_BASE = "com.licola.route";
  static final String PACKAGE_COMPILER=PACKAGE_BASE+".compiler";
  static final String PACKAGE_API=PACKAGE_BASE+".api";
  static final String PACKAGE_ANNOTATION=PACKAGE_BASE+".annotation";

  static final String ROUTE_CLASS_PREFIX = "Route";
  static final String ROUTE_CLASS_PROTOCOL_PREFIX = "RouteProtocol";

  static final String ROUTE_CLASS_INTERCEPTOR = "Interceptor";


  static final String ROUTE_ANNOTATION_PROTOCOL = "Protocol";//契约类注释名

  static final String ROUTE_METHOD_LOAD ="load";
  static final String ROUTE_METHOD_LOAD_PARAMETER ="map";
  static final String ROUTE_METHOD_NAVIGATION ="navigation";
  static final String ROUTE_METHOD_NAVIGATION_PARAMETER_1 ="target";
  static final String ROUTE_METHOD_NAVIGATION_PARAMETER_2 ="interceptor";





}
