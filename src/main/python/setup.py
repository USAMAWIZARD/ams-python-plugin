from Cython.Build import cythonize
import sysconfig
from setuptools import setup, Extension
import numpy

include_dirs = [sysconfig.get_paths()["include"]]
library_dirs = [sysconfig.get_config_var("LIBDIR")]
include_dirs.append(numpy.get_include())

libraries = [sysconfig.get_config_var(
    "LDLIBRARY").replace("lib", "").replace(".so", "")]

print(library_dirs)

setup(
    ext_modules=cythonize(
        Extension(
            "libpythonWrapper",
            ["./libpythonWrapper.pyx", "c_helper.c"],
            include_dirs=include_dirs,
            library_dirs=library_dirs,
            libraries=libraries,
            extra_compile_args=["-w"]
        ),
    )

)
