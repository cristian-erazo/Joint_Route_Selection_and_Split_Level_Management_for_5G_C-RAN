graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
  ]
  node [
    id 2
    label "2"
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 5
    prb 2
    x 33
    y 36
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 7
    prb 3
    x 71
    y 23
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 2
    prb 4
    x 45
    y 109
  ]
  edge [
    source 0
    target 4
    bandwith 435
    delay 449
  ]
  edge [
    source 1
    target 5
    bandwith 726
    delay 343
  ]
  edge [
    source 2
    target 3
    bandwith 703
    delay 418
  ]
]
