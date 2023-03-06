@echo off
cd Andromeda-Server >> NUL
git add . >> NUL
git commit -m "fixup" >> NUL
git format-patch -1 >> NUL
0001-fixup.patch >> NUL
cd .. >> NUL
echo Complete.
