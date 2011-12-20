Name:           mytools
Version:        1.0
Release:        6%{?dist}
Summary:        My own Java Tools

Group:          Applications/Internet
License:        GPL
URL:            http://
Source0:        %{name}-%{version}.tar.gz
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildArch:      noarch
BuildRequires:  java-1.6.0-openjdk, log4j
Requires:       java-1.6.0-openjdk, log4j

%description
Some classes that are reusable.
contains the Wget code.

%prep
%setup -q


%build
mkdir -p bin/zen/ilgo/tools
CP=".:/usr/share/java/log4j.jar"
javac -cp $CP -d bin `find ./ -name *.java`

cp src/log4j.properties bin
cd bin
jar cvMf %{name}.jar ./*


%install
rm -rf $RPM_BUILD_ROOT
install -d -m 755 $RPM_BUILD_ROOT%{_javadir}
install -m 644 bin/%{name}.jar $RPM_BUILD_ROOT%{_javadir}/%{name}-%{version}.jar


%clean
rm -rf $RPM_BUILD_ROOT

%post
cd %{_javadir}
ln -s %{name}-%{version}.jar %{name}.jar

%postun
cd %{_javadir}
unlink %{name}.jar

%files
%defattr(-,root,root,-)
%doc
%{_javadir}/%{name}-%{version}.jar


%changelog
* Sunday, July 24 2011 Roger Holenweger <ilgo711@gmail.com> 1.0-5
- added Stardict and JAXB classes for the DictEntry tags

* Fri Oct 23 2009 Roger Holenweger <ilgo711@gmail.com> 1.0-4
- added MyStringBuilder

* Wed Sep 18 2009 Roger Holenweger <ilgo711@gmail.com> 1.0-4
- added the Pipeline Framework to the jar
- added log4j to compile classpath

* Wed Sep 16 2009 Roger Holenweger <ilgo711@gmail.com> 1.0-3
- create a mytools.jar link in /usr/share/java

* Wed Sep 16 2009 Roger Holenweger <ilgo711@gmail.com> 1.0-2
- do not create a MANIFEST.MF file inside the jar

* Wed Sep 16 2009 Roger Holenweger <ilgo711@gmail.com> 1.0-1
- initial rpm
