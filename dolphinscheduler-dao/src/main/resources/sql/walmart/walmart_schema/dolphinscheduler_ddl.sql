alter table t_ds_process_definition drop receivers;
alter table t_ds_process_definition drop receivers_cc;
alter table t_ds_alertgroup drop group_type;
alter table t_ds_task_instance drop process_definition_id;
alter table t_ds_task_definition drop process_definition_id;