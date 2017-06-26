# jcommands
Jargon-based icommands console

## Prerequisites

- JDK 1.8

## Install

Download jcommands.zip and extract.  cd to that directory and run jcommands.sh from a terminal

```

mcc@mikesubuntu:~/Documents/testingjc/jcommands$ ./jcommands.sh 
              _  ____                                          _     
             (_)/ ___|___  _ __ ___  _ __ ___   __ _ _ __   __| |___ 
             | | |   / _ \| '_ ` _ \| '_ ` _ \ / _` | '_ \ / _` / __|
             | | |__| (_) | | | | | | | | | | | (_| | | | | (_| \__ \
            _/ |\____\___/|_| |_| |_|_| |_| |_|\__,_|_| |_|\__,_|___/
           |__/                                                      

Welcome to jCommands
iRODS>


```

#### HINT: Run help command to get available functions


Log in with iinit command

```
Welcome to jCommands
iRODS>iinit --host irods420.irodslocal --port 1247 --zone zone1 --user test1 --pwd test
logged in!
iRODS>


```

Sorry...pwd in clear for now...

Note that --resc switch can set default storage resource here

### current local commands

* pwd
* cd
* ls (no -la yet)
* mkdir
* rm

### current irods file commands

* ipwd
* icd
* ils
* irm
* imkdir



### data transfer


#### iput

iput (--local, --remote, --overwrite)  if remote is not specified it uses current irods directory

--overwrite true   will cause an overwrite of a file, if left off no overwrite

recursive not supported yet

right now the feedback just dumps status callbacks, we can make a nice progress bar later.  Here's an example 

```
iRODS>icd image1
/zone1/home/test1/braini/exp2/smple3/image1
iRODS>ils

    Channel1
    Channel2
    Channel3
    Channel4
iRODS>icd Channel1
/zone1/home/test1/braini/exp2/smple3/image1/Channel1
iRODS>ils

    analysis.png
iRODS>ipwd
/zone1/home/test1/braini/exp2/smple3/image1/Channel1
iRODS>pwd
/home/mcc/temp/hello
iRODS>iget --remote analysis.png


```

#### iget

iget (--local, --remote, --overwrite) if local is not specified it uses current local directory

Here's an example

```
iRODS>ipwd
/zone1/home/test1/boo/booyah/zip/zap
iRODS>iput --local Odum_irodsUsersGroup_June_2016.pptx

```

# Logging/debugging

Currently the logging option is DEBUG, we'll expose it in the console later.  The logs go to log.out in the same dir as the shell script






