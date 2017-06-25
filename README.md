# jcommands
Jargon-based icommands console

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

### current irods file commands

* ipwd
* icd
* ils

Note that absolute path, relative path, and .. are supported, but it's not super sophisticated yet on path resolution


### data transfer


#### iput

iput (--local, --remote, --overwrite)  if remote is not specified it uses current irods directory

--overwrite true   will cause an overwrite of a file, if left off no overwrite

recursive not supported yet

right now the feedback just dumps status callbacks, we can make a nice progress bar later


#### iget

Work in progress

I should have time to add mkdir amd rm in time for Monday


# Logging/debugging

Currently the logging option is DEBUG, we'll expose it in the console later.  The logs go to log.out in the same dir as the shell script






