#!/bin/bash
# echo commands to the terminal output
set -ex

export DFS_NAMENODE_NAME_DIR=${DFS_NAMENODE_NAME_DIR:-/opt/namenode}
export DFS_DATANODE_DATA_DIR=${DFS_DATANODE_DATA_DIR:-/opt/datanode}

format_hdfs()
{
      echo Formatting $DFS_NAMENODE_NAME_DIR
      hdfs namenode -format -force
}

HADOOP_CMD="$1"
case "$HADOOP_CMD" in

  namenode)
  shift 1
    CMD=(hdfs namenode "$@")
    if [ -d "$DFS_NAMENODE_NAME_DIR" ]; then
      if [ "$(ls -A $DFS_NAMENODE_NAME_DIR)" ]; then
        echo $DFS_NAMENODE_NAME_DIR is not empty, skipping
      else
        echo $DFS_NAMENODE_NAME_DIR is empty
        format_hdfs
      fi
    else
    echo Directory not found, creating $DFS_NAMENODE_NAME_DIR
      mkdir -p $DFS_NAMENODE_NAME_DIR
      format_hdfs
    fi
    ;;

  datanode)
  shift 1
    CMD=(hdfs datanode "$@")
    if [ -d "$DFS_DATANODE_DATA_DIR" ]; then
      if [ "$(ls -A $DFS_DATANODE_DATA_DIR)" ]; then
        echo $DFS_DATANODE_DATA_DIR is not empty
      else
        echo $DFS_DATANODE_DATA_DIR is empty
      fi
    else
    echo Directory not found, creating $DFS_DATANODE_DATA_DIR
      mkdir -p $DFS_DATANODE_DATA_DIR
    fi
    ;;

  historyserver)
  shift 1
    CMD=(yarn historyserver "$@")
    ;;

  proxyserver)
  shift 1
    CMD=(yarn proxyserver "$@")
    ;;

  nodemanager)
  shift 1
    CMD=(yarn nodemanager "$@")
    ;;

  resourcemanager)
  shift 1
    CMD=(yarn resourcemanager "$@")
    ;;

  *)
    echo "Unknown command: $HADOOP_CMD" 1>&2
    exit 1
esac

# Execute the container CMD under tini for better hygiene
env
exec tini -s -- "${CMD[@]}"