package de.bybackfish.telosaddons.utils

fun getAnnotation(annotation: Annotation, clazz: Class<*>): Annotation? {
    return clazz.getAnnotation(annotation.annotationClass.java)
}