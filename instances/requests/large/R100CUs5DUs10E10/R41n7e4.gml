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
    prc 2
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
    prc 3
    ant 9
    prb 6
    x 98
    y 67
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 4
    prb 2
    x 41
    y 22
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 8
    prb 6
    x 31
    y 66
  ]
  node [
    id 6
    label "6"
    type 1
    prc 2
    ant 4
    prb 5
    x 56
    y 60
  ]
  edge [
    source 0
    target 3
    bandwith 138
    delay 350
  ]
  edge [
    source 1
    target 6
    bandwith 724
    delay 157
  ]
  edge [
    source 2
    target 4
    bandwith 581
    delay 300
  ]
  edge [
    source 2
    target 5
    bandwith 961
    delay 368
  ]
]
