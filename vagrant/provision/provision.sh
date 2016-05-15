#!/bin/sh

JDK_VERSION=8u91
JDK_NAME=jdk1.8.0_91
JDK_ARCHIVE=jdk-${JDK_VERSION}-linux-x64.rpm
JDK_URL=http://download.oracle.com/otn-pub/java/jdk/${JDK_VERSION}-b14/${JDK_ARCHIVE}

GRADLE_VERSION=2.13
GRADLE_ARCHIVE=gradle-${GRADLE_VERSION}-bin.zip
GRADLE_URL=https://services.gradle.org/distributions/${GRADLE_ARCHIVE}

# lang
sed -i s/LANG=.*/LANG=ja_JP.UTF-8/g /etc/locale.conf

# localtime
ln -fs /usr/share/zoneinfo/Asia/Tokyo /etc/localtime

# vim
if [ ! `yum list installed | grep vim` ] ; then
  yum install -qy vim
fi

# man-pages-ja
if [ ! `yum list installed | grep man-pages-ja` ] ; then
  yum install -qy man-pages-ja
fi

# unzip
if [ ! `yum list installed | grep unzip` ] ; then
  yum install -qy unzip
fi

# jdk
if [ ! `rpm -qa | grep ${JDK_NAME}` ] ; then
  wget -q --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" ${JDK_URL}
  rpm --quiet -ivh ${JDK_ARCHIVE}
  rm -f ${JDK_ARCHIVE}
  cat <<- EOL > jdk.sh
	#!/bin/sh
	export JAVA_HOME=/usr/java/${JDK_NAME}
	export PATH=\$PATH:\$JAVA_HOME/bin
EOL
  chown root:root jdk.sh
  chmod 644 jdk.sh
  mv jdk.sh /etc/profile.d/jdk.sh
fi

if [ ! `gradle -version` ] ; then
  wget -q ${GRADLE_URL}
  unzip -q ${GRADLE_ARCHIVE}
  rm -f ${GRADLE_ARCHIVE}
  mkdir /opt/gradle
  mv gradle-${GRADLE_VERSION} /opt/gradle/gradle-${GRADLE_VERSION}
  cat <<- EOL > gradle.sh
	#!/bin/sh
	export GRADLE_HOME=/opt/gradle/gradle-${GRADLE_VERSION}
	export PATH=\$PATH:\$GRADLE_HOME/bin
EOL
  chown root:root gradle.sh
  chmod 644 gradle.sh
  mv gradle.sh /etc/profile.d/gradle.sh
fi